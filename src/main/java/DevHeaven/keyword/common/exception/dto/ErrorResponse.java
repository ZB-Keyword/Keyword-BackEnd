package DevHeaven.keyword.common.exception.dto;

import DevHeaven.keyword.common.exception.CustomException;
import DevHeaven.keyword.common.exception.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private ErrorCode errorCode;
    private String errorMessage;
    private HttpStatus httpStatus;

    public static ErrorResponse from(final ErrorCode errorCode) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorCode.getErrorMessage())
                .httpStatus(errorCode.getHttpStatus())
                .build();
    }

    public static ErrorResponse from(final Exception exception, final ErrorCode errorCode) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(exception.getMessage())
                .httpStatus(errorCode.getHttpStatus())
                .build();
    }

    public static ErrorResponse from(final CustomException exception){
        return ErrorResponse.builder()
            .errorCode(exception.getErrorCode())
            .errorMessage(exception.getErrorCode().getErrorMessage())
            .httpStatus(exception.getErrorCode().getHttpStatus())
            .build();
    }

    public static ErrorResponse from(final String errorMessage, final ErrorCode errorCode) {
        return ErrorResponse.builder()
            .errorCode(errorCode)
            .errorMessage(errorMessage)
            .httpStatus(errorCode.getHttpStatus())
            .build();
    }

    public static String getFirstErrorMessageByBindingResults(final BindingResult bindingResult) {
        if(!bindingResult.hasErrors()) {
            return "No errors were found in binding result";
        }

        FieldError firstFieldError = bindingResult.getFieldErrors().get(0);
        return firstFieldError.getField() + " : " + firstFieldError.getDefaultMessage();
    }
}
