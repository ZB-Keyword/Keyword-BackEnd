package DevHeaven.keyword.domain.schedule.dto.response;

import DevHeaven.keyword.domain.schedule.dto.ScheduleFriend;
import DevHeaven.keyword.domain.schedule.type.ScheduleStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ScheduleDetailResponse {
    private Long organizerId;
    private String title;
    private String contents;
    private LocalDateTime scheduleAt;
    private String locationExplanation;
    private Double latitude;
    private Double longitude;
    private ScheduleStatus status;
    private Long remindAt;
    private List<ScheduleFriend> scheduleFriendList;
}
