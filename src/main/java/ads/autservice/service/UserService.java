package ads.autservice.service;

import ads.autservice.constant.ErrorEnum;
import ads.autservice.dto.UserDetailResponse;
import ads.autservice.entity.User;
import ads.autservice.exception.GenericException;
import ads.autservice.mapper.UserMapper;
import ads.autservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

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
}
