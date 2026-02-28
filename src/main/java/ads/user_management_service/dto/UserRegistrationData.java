package ads.user_management_service.dto;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationData {

    private UUID id;
    private String fullName;
    private String email;
    private Integer roleId;
}