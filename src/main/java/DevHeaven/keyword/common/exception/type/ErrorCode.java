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

    ALREADY_EXISTS_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    ALREADY_EXISTS_PHONE(HttpStatus.BAD_REQUEST, "이미 존재하는 핸드폰 번호입니다."),

    BLOCKED_MEMBER(HttpStatus.BAD_REQUEST, "관리자에 의해 정지된 회원입니다."),
    INACTIVE_MEMBER(HttpStatus.BAD_REQUEST, "비활성화된 회원입니다."),
    WITHDRAWN_MEMBER(HttpStatus.BAD_REQUEST, "탈퇴한 회원입니다."),

    MISMATCH_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    MISMATCH_PROVIDER(HttpStatus.BAD_REQUEST, "소셜 회원은 소셜 로그인을 통해 접근해야 합니다."),

    OAUTH_PROVIDER_NOT_VALID_EXCEPTION(HttpStatus.BAD_REQUEST, "제공되는 유효한 소셜 로그인 요청이 아닙니다."),

    // friend
    FRIEND_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 친구를 찾을 수 없습니다."),
    FILE_NOT_EXIST(HttpStatus.BAD_REQUEST, "업로드 될 파일이 존재하지 않습니다."),
    FILE_INCORRECT_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일입니다"),
    FRIEND_REQUEST_ALREADY(HttpStatus.BAD_REQUEST, "이미 요청한 친구쪽에서 친구 요청을 보냈습니다"),
    FRIEND_NOT_VALID_REQUEST(HttpStatus.BAD_REQUEST, "친구 잘못된 요청입니다" ),
    FRIEND_ALREADY(HttpStatus.BAD_REQUEST, "이미 친구 요청을 하였습니다"),

    // chat
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."),

    // schedule
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 일정을 찾을 수 없습니다."),
    SCHEDULE_MEMBER_UNMATCHED(HttpStatus.BAD_REQUEST, "해당 일정에 포함된 회원이 아닙니다."),
    MEMBER_NOT_ORGANIZER(HttpStatus.BAD_REQUEST, "해당 일정의 주최자가 아닙니다.");

    private final HttpStatus httpStatus;
    private final String errorMessage;
}
