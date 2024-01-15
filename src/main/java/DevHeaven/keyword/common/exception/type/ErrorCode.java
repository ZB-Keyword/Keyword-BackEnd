package DevHeaven.keyword.common.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생하였습니다."),
    METHOD_ARGUMENT_NOT_VALID_EXCEPTION(HttpStatus.BAD_REQUEST, "전달된 정보 중 올바르지 않은 값이 전달되었습니다."),

    // jwt
    EXPIRED_JWT_EXCEPTION(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    UNSUPPORTED_JWT_EXCEPTION(HttpStatus.BAD_REQUEST, "지원되지 않는 토큰입니다."),
    MALFORMED_JWT_EXCEPTION(HttpStatus.BAD_REQUEST, "잘못된 형식의 토큰입니다."),
    SIGNATURE_EXCEPTION(HttpStatus.BAD_REQUEST, "토큰 서명이 올바르지 않습니다."),
    ILLEGAL_ARGUMENT_EXCEPTION(HttpStatus.BAD_REQUEST, "잘못된 인자가 전달되었습니다."),
    UNAUTHENTICATED_JWT_EXCEPTION(HttpStatus.BAD_REQUEST, "토큰 회원 인증에 실패하였습니다."),
    ACCESS_DENIED_JWT_EXCEPTION(HttpStatus.BAD_REQUEST, "올바른 권한의 접근이 아닙니다."),

    REFRESH_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "요청한 회원의 refresh token 을 찾을 수 없습니다."),

    REFRESH_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "요청한 회원의 refresh token 이 만료되었습니다."),

    // member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 회원을 찾을 수 없습니다."),
    EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 이메일을 찾을 수 없습니다."),

    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 회원을 찾을 수 없습니다."),

    ALREADY_EXISTS_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),

    BLOCKED_MEMBER(HttpStatus.BAD_REQUEST, "관리자에 의해 정지된 회원입니다."),
    INACTIVE_MEMBER(HttpStatus.BAD_REQUEST, "비활성화된 회원입니다."),
    WITHDRAWN_MEMBER(HttpStatus.BAD_REQUEST, "탈퇴한 회원입니다."),

    MISMATCH_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
  
    // friend
    FRIEND_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 친구를 찾을 수 없습니다."),
    FILE_NOT_EXIST(HttpStatus.BAD_REQUEST, "업로드 될 파일이 존재하지 않습니다."),
    FILE_INCORRECT_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일입니다"),


    // chat
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다.");
    private final HttpStatus httpStatus;
    private final String errorMessage;
}
