package ads.autservice.dto;

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