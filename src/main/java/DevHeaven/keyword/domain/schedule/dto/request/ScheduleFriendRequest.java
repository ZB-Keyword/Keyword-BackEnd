package DevHeaven.keyword.domain.schedule.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ScheduleFriendRequest {
  private Long memberId;
  private String name;

}