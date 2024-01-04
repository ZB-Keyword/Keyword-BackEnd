package DevHeaven.keyword.common.exception;

import DevHeaven.keyword.common.exception.type.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

  private final ErrorCode errorCode;
  public CustomException(ErrorCode errorCode) {
    this.errorCode = errorCode;
  }
}
