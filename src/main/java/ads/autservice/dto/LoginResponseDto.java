package ads.autservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto implements Serializable {
	
	private static final long serialVersionUID = -6464592482733456181L;
	
	private String token;

	private String fullName;
//	private String email;
//	private String roleName;
//	private String roleDescription;

	
}
