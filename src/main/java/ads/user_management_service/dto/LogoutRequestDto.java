package ads.user_management_service.dto;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequestDto implements Serializable {

	private static final long serialVersionUID = -4901497287714401529L;
	
	private String token;
	
}
