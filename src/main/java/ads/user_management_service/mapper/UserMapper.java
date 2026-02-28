package ads.user_management_service.mapper;

import ads.user_management_service.dto.UserDetailResponse;
import ads.user_management_service.entity.User;

public class UserMapper {
    private UserMapper() {}

    public static UserDetailResponse toDetailResponse(User user) {
        return UserDetailResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .roleName(user.getRole().getRoleName())
                .roleDescription(user.getRole().getRoleDescription())
                .build();
    }
}
