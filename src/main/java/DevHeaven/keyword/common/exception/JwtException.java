package DevHeaven.keyword.common.exception;

import DevHeaven.keyword.common.exception.type.ErrorCode;

public class JwtException extends CustomException {

  public JwtException(final ErrorCode errorCode) { super(errorCode); }
}
