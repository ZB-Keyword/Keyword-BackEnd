package DevHeaven.keyword.common.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생하였습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 회원을 찾을 수 없습니다."),

    FRIEND_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 친구를 찾을 수 없습니다."),
    FRIEND_ALREADY(HttpStatus.BAD_REQUEST, "이미 친구 입니다."),
    FRIEND_REQUEST_ALREADY(HttpStatus.BAD_REQUEST, "이미 요청한 친구쪽에서 친구 요청을 보냈습니다"),
    FRIEND_NOT_VALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 친구 요청입니다.");
    private final HttpStatus httpStatus;
    private final String errorMessage;
}
