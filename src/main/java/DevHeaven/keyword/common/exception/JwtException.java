package DevHeaven.keyword.common.exception;

import DevHeaven.keyword.common.exception.type.ErrorCode;
import lombok.Getter;

@Getter
public class JwtException extends CustomException {

  public JwtException(final ErrorCode errorCode) { super(errorCode); }
}
