package ads.user_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogoutResponseDto implements Serializable {

	private static final long serialVersionUID = -2294930740592870555L;
	
	private Boolean isSuccess;
	
}
