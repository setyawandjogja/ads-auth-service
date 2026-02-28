package ads.user_management_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> implements Serializable {
	@Serial
	private static final long serialVersionUID = -6901867171752079438L;

	private String operationId;
	private String errorCode;
	private String errorDesc;
	private String errorSource;
	private String errorDetail;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
	private LocalDateTime timestamp;
	private transient T data;

	public LocalDateTime getTimestamp() {
		return Objects.isNull(timestamp) ? LocalDateTime.now() : timestamp;
	}
}
