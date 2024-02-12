package DevHeaven.keyword.domain.notice.dto.response;

import DevHeaven.keyword.domain.notice.entity.Notice;
import DevHeaven.keyword.domain.notice.type.NoticeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeResponse {

  private Long noticeId;
  private Long infoId;
  private NoticeType type;

  public static NoticeResponse from(final Notice notification) {
    return NoticeResponse.builder()
        .noticeId(notification.getId())
        .infoId(notification.getInformationId())
        .type(notification.getType())
        .build();
  }
}
