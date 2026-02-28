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
public class ValidateResponseDto implements Serializable {

	private static final long serialVersionUID = 1504150777535128875L;
	
	private Long userId;
	
}
