package DevHeaven.keyword.domain.schedule.dto.response;

import DevHeaven.keyword.domain.schedule.type.ScheduleStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ScheduleListResponse {
    private Long scheduleId;
    private String title;
    private LocalDateTime scheduleDateTime;
    private String locationExplanation;
    private ScheduleStatus status;
}
