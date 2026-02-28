package ads.user_management_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserRegistrationData {

    private UUID id;
    private String fullName;
    private String email;
    private Integer roleId;
}