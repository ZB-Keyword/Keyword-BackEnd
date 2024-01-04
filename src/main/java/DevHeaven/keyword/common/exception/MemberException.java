package DevHeaven.keyword.common.exception;

import DevHeaven.keyword.common.exception.type.ErrorCode;
import lombok.Getter;

@Getter
public class MemberException extends CustomException{

  public MemberException(final ErrorCode errorCode) {
    super(errorCode);
  }
}
