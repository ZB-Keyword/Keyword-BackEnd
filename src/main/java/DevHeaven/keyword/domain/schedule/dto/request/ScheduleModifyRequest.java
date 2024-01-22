package DevHeaven.keyword.domain.schedule.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleModifyRequest {

  @NotNull
  private String title;

  @NotNull
  private String contents;

  @NotNull
  private LocalDateTime scheduleAt;

  @NotNull
  private String locationExplanation;

  @NotNull
  private Double latitude;

  @NotNull
  private Double longitude;

  @NotNull
  private Long remindAt;

  private List<ScheduleFriendRequest> scheduleFriendList;

}