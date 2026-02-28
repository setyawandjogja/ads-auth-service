package ads.user_management_service.service;

import ads.user_management_service.constant.ErrorEnum;
import ads.user_management_service.dto.CreateUserRequestDto;
import ads.user_management_service.entity.User;
import ads.user_management_service.exception.GenericException;
import ads.user_management_service.repository.RoleRepository;
import ads.user_management_service.repository.UserRepository;
import ads.user_management_service.util.Md5Util;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            rabbitTemplate.convertAndSend("amq.direct", "health.test", "PING");
            rabbitAvailable = true;
            log.info("RabbitMQ connection available");
        } catch (Exception e) {
            rabbitAvailable = false;
            log.warn("RabbitMQ unavailable: {}", e.getMessage());
        }
    }

    // ===============================
    // JWT VALIDATION
    // ===============================
    private String validateAndExtractToken(String authHeader) {

        if (authHeader == null || authHeader.isBlank()) {
            throw new GenericException(
                    ErrorEnum.UNAUTHORIZED,
                    "Authorization required"
            );
        }

        if (!authHeader.startsWith("Bearer ")) {
            throw new GenericException(
                    ErrorEnum.UNAUTHORIZED,
                    "Invalid Authorization format"
            );
        }

        return authHeader.substring(7);
    }

    // ===============================
    // UPDATE PASSWORD
    // ===============================
    @Transactional
    public void updatePassword(UUID userId, String newPassword, String authHeader) {

        String token = validateAndExtractToken(authHeader);

        String role = jwtService.extractRole(token);
        UUID currentUserId = UUID.fromString(jwtService.extractUserId(token));

        // hanya ADMIN atau user sendiri
        if (!"ADMIN".equalsIgnoreCase(role) && !currentUserId.equals(userId)) {
            throw new GenericException(
                    ErrorEnum.UNAUTHORIZED,
                    "Cannot update other user's password"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GenericException(
                        ErrorEnum.DATA_NOT_FOUND,
                        "User not found"
                ));

        user.setPassword(Md5Util.md5(newPassword));
    }

    // ===============================
    // CREATE USER
    // ===============================
    @Transactional
    public void createUser(CreateUserRequestDto req, String authHeader) {

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
                        ErrorEnum.DATA_NOT_FOUND,
                        "Role not found"
                ));

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUserName(req.getUsername());
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPassword(Md5Util.md5(req.getPassword()));
        user.setRole(role);

        userRepository.save(user);

        UUID createdBy = UUID.fromString(jwtService.extractUserId(token));

        if (rabbitAvailable) {
            try {
                publisher.publishUserCreated(user, createdBy);
            } catch (Exception e) {
                log.warn("Failed to publish RabbitMQ event: {}", e.getMessage());
            }
        }
    }
}