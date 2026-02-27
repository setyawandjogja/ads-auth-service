package ads.autservice.exception;

import ads.autservice.constant.ErrorEnum;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder
public class GenericException extends Exception {

	@Serial
	private static final long serialVersionUID = 5444171877399813880L;
	
	private final ErrorEnum error;
	
	public GenericException (ErrorEnum error) {
		super(error.getErrorDesc());
		error.setErrorDetail(error.getErrorDesc());
		this.error = error;
	}
	
	public GenericException (ErrorEnum error, String message) {
		super(message);
		error.setErrorDetail(message);
		this.error = error;
	}

	public GenericException (ErrorEnum error, String message, Object data) {
		super(message);
		error.setErrorDetail(message);
		error.setAdditionalData(data);
		this.error = error;
	}

	public GenericException (ErrorEnum error, Object data) {
		super(error.getErrorDesc());
		error.setErrorDetail(error.getErrorDesc());
		error.setAdditionalData(data);
		this.error = error;
	}
	
}
