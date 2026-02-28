package ads.autservice.service;

import ads.autservice.constant.ErrorEnum;
import ads.autservice.dto.CreateUserRequestDto;
import ads.autservice.dto.UserDetailResponse;
import ads.autservice.entity.User;
import ads.autservice.exception.GenericException;
import ads.autservice.mapper.UserMapper;
import ads.autservice.repository.RoleRepository;
import ads.autservice.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ads.autservice.util.Md5Util;

import java.time.Duration;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RedisService redisService;
    private final UserEventPublisher publisher;
    private final RabbitTemplate rabbitTemplate;
    private final JwtService jwtService;

    private static final Duration CACHE_TTL = Duration.ofHours(24);
    private boolean rabbitAvailable = false;

    @PostConstruct
    public void checkRabbitConnection() {
        try {
            // coba kirim test message tanpa throw exception
            rabbitTemplate.convertAndSend("amq.direct", "health.test", "PING");
            rabbitAvailable = true;
            log.info("RabbitMQ connection available");
        } catch (Exception e) {
            rabbitAvailable = false;
            log.info("RabbitMQ unavailable: {}", e.getMessage());
        }
    }


    // Helper Method untuk validasi JWT
    private String validateAndExtractToken(String authHeader) throws GenericException {

        if (authHeader == null || authHeader.isBlank()) {
            throw new GenericException(ErrorEnum.UNAUTHORIZED, "Authorization required");
        }

        if (!authHeader.startsWith("Bearer ")) {
            throw new GenericException(ErrorEnum.UNAUTHORIZED, "Invalid Authorization format");
        }

        return authHeader.substring(7);
    }

    @Transactional
    public void updatePassword(UUID userId, String newPassword, String authHeader)
            throws GenericException {

        String token = validateAndExtractToken(authHeader);

        String role = jwtService.extractRole(token);
        UUID currentUserId = UUID.fromString(jwtService.extractUserId(token));

        // hanya ADMIN atau user sendiri
        if (!"ADMIN".equalsIgnoreCase(role) && !currentUserId.equals(userId)) {
            throw new GenericException(ErrorEnum.UNAUTHORIZED,
                    "Cannot update other user's password");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GenericException(
                        ErrorEnum.DATA_NOT_FOUND, "User not found"));

        // enkripsi password (konsisten dengan createUser)
        user.setPassword(Md5Util.md5(newPassword));
    }

    @Transactional
    public void createUser(CreateUserRequestDto req, String authHeader)
            throws GenericException {

        String token = validateAndExtractToken(authHeader);

        String roleFromToken = jwtService.extractRole(token);

        if (!"ADMIN".equalsIgnoreCase(roleFromToken)) {
            throw new GenericException(ErrorEnum.UNAUTHORIZED);
        }

        if (userRepository.existsByUserName(req.getUsername())) {
            throw new GenericException(ErrorEnum.USER_ALREADY_EXIST);
        }

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new GenericException(ErrorEnum.EMAIL_ALREADY_EXIST);
        }

        var role = roleRepository.findById(req.getRoleId())
                .orElseThrow(() -> new GenericException(
                        ErrorEnum.DATA_NOT_FOUND, "Role not found"));

        String encryptedPassword = Md5Util.md5(req.getPassword());

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUserName(req.getUsername());
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPassword(encryptedPassword);
        user.setRole(role);

        userRepository.save(user);

        String userIdFromToken = jwtService.extractUserId(token);
        UUID createdBy = UUID.fromString(userIdFromToken);
        // Rabbit publish
        if (rabbitAvailable) {
            try {
                publisher.publishUserCreated(user,createdBy);
            } catch (Exception e) {
                log.info("Failed to publish RabbitMQ event: {}", e.getMessage());
            }
        } else {
            log.info("Skipping RabbitMQ publish, connection unavailable");
        }
    }
}