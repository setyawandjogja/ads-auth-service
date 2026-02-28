package ads.autservice.exception;

import ads.autservice.constant.ErrorEnum;
import ads.autservice.dto.BaseResponse;
import ads.autservice.util.BaseResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GenericException.class)
    public BaseResponse<Void> handleGenericException(GenericException ex) {
        log.error("GenericException: {}", ex.getMessage());

        return BaseResponseUtils.constructResponse(
                ex.getError(),
                null
        );
    }

    @ExceptionHandler(Exception.class)
    public BaseResponse<Void> handleException(Exception ex) {
        log.error("Unhandled Exception: ", ex);

        return BaseResponseUtils.constructResponse(
                ErrorEnum.DEFAULT_ERROR,
                null
        );
    }
}