package DevHeaven.keyword.domain.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ScheduleFriend {
    private Long memberId;
    private String name;

}
