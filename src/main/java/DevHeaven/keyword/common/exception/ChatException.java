package DevHeaven.keyword.common.exception;

import DevHeaven.keyword.common.exception.type.ErrorCode;
import lombok.Getter;

@Getter
public class ChatException extends CustomException {

  public ChatException(ErrorCode errorCode) {
    super(errorCode);
  }
}
