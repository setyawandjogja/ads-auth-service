package ads.user_management_service.dto;


import lombok.*;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto implements Serializable {

	private static final long serialVersionUID = 1727060555188695252L;
	
	private String username;
	private String password;
	
}
