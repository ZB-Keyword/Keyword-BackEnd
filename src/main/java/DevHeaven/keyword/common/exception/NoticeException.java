package DevHeaven.keyword.common.exception;

import DevHeaven.keyword.common.exception.type.ErrorCode;
import lombok.Getter;

@Getter
public class NoticeException extends CustomException{

  public NoticeException(ErrorCode errorCode) {
    super(errorCode);
  }
}
