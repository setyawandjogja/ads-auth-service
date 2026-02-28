package ads.autservice.util;

import ads.autservice.constant.ErrorEnum;
import ads.autservice.dto.BaseResponse;
import ads.autservice.exception.GenericException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BaseResponseUtils {

	private static String applicationName;

	@Value("${spring.application.name}")
	public void setApplicationName(String name) {
		applicationName = name;
	}

	public static ErrorEnum getErrorCode(Throwable t) {
		if (t instanceof GenericException ex) {
			return ex.getError();
		} else {
			return ErrorEnum.DEFAULT_ERROR;
		}
	}

	public static <T> BaseResponse<T> constructResponse(ErrorEnum error, T data) {

		return BaseResponse.<T>builder()
				.errorCode(error.getErrorCode())
				.errorDesc(error.getErrorDesc())
				.errorSource(applicationName)
				.errorDetail(error.getErrorDetail())
				.operationId(generateOperationId())
				.data(data)
				.build();
	}

	public static <T> BaseResponse<T> constructSuccessResponse(T data) {

		return BaseResponse.<T>builder()
				.errorCode(ErrorEnum.SUCCESS.getErrorCode())
				.errorDesc(ErrorEnum.SUCCESS.getErrorDesc())
				.errorSource(applicationName)
				.errorDetail(ErrorEnum.SUCCESS.getErrorDetail())
				.operationId(generateOperationId()) 
				.data(data)
				.build();
	}

	private static String generateOperationId() {
		return UUID.randomUUID().toString();
	}

	public static <T> boolean isSuccess(BaseResponse<T> response) {
		return (response != null)
				&& ErrorEnum.SUCCESS.getErrorCode().equals(response.getErrorCode());
	}

	public static <T> BaseResponse<T> checkResponse(BaseResponse<T> response)
			throws GenericException {

		if (response == null) {
			throw new GenericException(ErrorEnum.DEFAULT_ERROR,
					"Base response is empty");
		}

		if (isSuccess(response)) {
			return response;
		} else {
			throw new GenericException(
					ErrorEnum.getErrorEnumByCode(response.getErrorCode()),
					response.getErrorDetail()
			);
		}
	}
}