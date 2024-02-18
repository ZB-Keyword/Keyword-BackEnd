package DevHeaven.keyword.domain.notice.dto.response;

import DevHeaven.keyword.domain.notice.entity.Notice;
import DevHeaven.keyword.domain.notice.type.NoticeType;
import DevHeaven.keyword.domain.schedule.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeListResponse {

  private Long noticeId;
  private Long infoId;
  private NoticeType type;
  private Long scheduleId;
  public static NoticeListResponse from(final Notice notification, final Schedule schedule) {
    return NoticeListResponse.builder()
        .noticeId(notification.getId())
        .infoId(notification.getInformationId())
        .type(notification.getType())
        .scheduleId(schedule.getScheduleId())
        .build();
  }
}
