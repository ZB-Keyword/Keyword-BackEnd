package DevHeaven.keyword.common.exception;

import DevHeaven.keyword.common.exception.type.ErrorCode;
import lombok.Getter;

@Getter
public class FriendNotFoundException extends CustomException {

  public FriendNotFoundException(ErrorCode errorCode) {
    super(errorCode);
  }
}
