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
public class ValidateRequestDto implements Serializable {

	private static final long serialVersionUID = 3524010912843188578L;
	
	private String token;
	
}
