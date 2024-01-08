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
    FILE_NOT_EXIST(HttpStatus.BAD_REQUEST, "업로드 될 파일이 존재하지 않습니다."),
    FILE_INCORRECT_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일입니다");

    private final HttpStatus httpStatus;
    private final String errorMessage;
}
