package DevHeaven.keyword.common.exception;

import DevHeaven.keyword.common.exception.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static DevHeaven.keyword.common.exception.type.ErrorCode.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e){
        log.error("Exception is occurred.", e);

        return ResponseEntity.status(INTERNAL_SERVER_ERROR.getHttpStatus()).body(ErrorResponse.from(e, INTERNAL_SERVER_ERROR));
    }
}
