package DevHeaven.keyword.common.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생하였습니다."),


    ;

    private final HttpStatus httpStatus;
    private final String errorMessage;
}
