package DevHeaven.keyword.domain.schedule.dto.request;

import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.schedule.type.ScheduleStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScheduleCreateRequest {

  private String title;

  private String contents;

  private LocalDateTime scheduleAt;

  private String locationExplanation;

  private Double latitude;

  private Double longitude;

  private Long remindAt;

  private List<ScheduleFriendRequest> scheduleFriendList;

}