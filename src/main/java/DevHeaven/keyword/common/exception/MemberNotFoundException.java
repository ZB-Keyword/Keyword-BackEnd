package DevHeaven.keyword.common.exception;

import DevHeaven.keyword.common.exception.type.ErrorCode;
import lombok.Getter;

@Getter
public class MemberNotFoundException extends CustomException{

  public MemberNotFoundException(final ErrorCode errorCode) {
    super(errorCode);
  }
}
