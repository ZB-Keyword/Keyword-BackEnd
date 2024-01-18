package DevHeaven.keyword.domain.schedule.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScheduleModifyResponse {

  private Long scheduleId;
  private Long remindAt;

}