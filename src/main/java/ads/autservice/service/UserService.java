package ads.autservice.service;

import ads.autservice.constant.ErrorEnum;
import ads.autservice.dto.CreateUserRequestDto;
import ads.autservice.dto.UserDetailResponse;
import ads.autservice.entity.User;
import ads.autservice.exception.GenericException;
import ads.autservice.mapper.UserMapper;
import ads.autservice.repository.RoleRepository;
import ads.autservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ads.autservice.util.Md5Util;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RedisService redisService;

    private static final Duration CACHE_TTL = Duration.ofHours(24);
    @Transactional(readOnly = true)
    public UserDetailResponse getUserDetail(UUID userId) throws GenericException {

        User user = userRepository.findByIdWithRole(userId)
                .orElseThrow(() -> new GenericException(ErrorEnum.DATA_NOT_FOUND, "User Gam not found"));

        return UserMapper.toDetailResponse(user);
    }

    @Transactional
    public void updatePassword(UUID userId, String newPassword) throws GenericException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GenericException(ErrorEnum.DATA_NOT_FOUND, "User Gam not found"));

        user.setPassword(newPassword);

        // Tidak perlu save()
        // Hibernate dirty checking otomatis update saat commit
    }

    @Transactional
    public void createUser(CreateUserRequestDto req, String roleFromToken)
            throws GenericException {

        // 🔐 ADMIN ONLY
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

        //  tidak aman untuk production
        String encryptedPassword = Md5Util.md5(req.getPassword());

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUserName(req.getUsername());
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPassword(encryptedPassword);
        user.setRole(role);

        userRepository.save(user);

        //Cache to Redis
        String redisKey = "USER_CREDENTIAL:" + user.getUserName();
        redisService.setData(redisKey, user, CACHE_TTL);

        // publisher.publishUserCreated(user);
    }
}
