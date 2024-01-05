package DevHeaven.keyword.common.exception;

import DevHeaven.keyword.common.exception.type.ErrorCode;
import lombok.Getter;

@Getter
public class ScheduleException extends CustomException{

    public ScheduleException(ErrorCode errorCode) {
        super(errorCode);
    }
}
