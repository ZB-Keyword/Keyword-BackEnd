package DevHeaven.keyword.common.exception;

import DevHeaven.keyword.common.exception.type.ErrorCode;
import lombok.Getter;

@Getter
public class FriendException extends CustomException {

  public FriendException(ErrorCode errorCode) {
    super(errorCode);
  }
}
