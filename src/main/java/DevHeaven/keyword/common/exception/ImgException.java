package DevHeaven.keyword.common.exception;

import DevHeaven.keyword.common.exception.type.ErrorCode;
import lombok.Getter;

@Getter
public class ImgException extends CustomException {
  public ImgException(ErrorCode errorCode) {
    super(errorCode);
  }
}
