package ads.user_management_service.dto;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String newPassword;
}