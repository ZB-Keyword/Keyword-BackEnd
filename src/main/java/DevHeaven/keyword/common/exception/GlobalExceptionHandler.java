package DevHeaven.keyword.common.exception;

import DevHeaven.keyword.common.exception.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static DevHeaven.keyword.common.exception.dto.ErrorResponse.getFirstErrorMessageByBindingResults;
import static DevHeaven.keyword.common.exception.type.ErrorCode.INTERNAL_SERVER_ERROR;
import static DevHeaven.keyword.common.exception.type.ErrorCode.METHOD_ARGUMENT_NOT_VALID_EXCEPTION;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception exception) {
    log.error("{} : {}", exception.getClass().getName(), exception.getMessage());

    return ResponseEntity.status(INTERNAL_SERVER_ERROR.getHttpStatus())
        .body(ErrorResponse.from(exception, INTERNAL_SERVER_ERROR));
  }

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ErrorResponse> handleCustomException(CustomException exception) {
    log.error("{} : {}", exception.getClass().getName(), exception.getMessage());

    return ResponseEntity.status(exception.getErrorCode().getHttpStatus())
        .body(ErrorResponse.from(exception));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidException(
      MethodArgumentNotValidException exception) {
    log.error("{} : {}", exception.getClass().getName(),
        exception.getBindingResult().getFieldErrors().get(0).toString());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.from(getFirstErrorMessageByBindingResults(exception.getBindingResult()),
            METHOD_ARGUMENT_NOT_VALID_EXCEPTION));
  }
}
