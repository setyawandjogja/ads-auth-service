package ads.autservice.mapper;

import ads.autservice.dto.UserDetailResponse;
import ads.autservice.entity.User;

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
