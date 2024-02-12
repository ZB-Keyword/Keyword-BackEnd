package DevHeaven.keyword.domain.notice.dto;

import DevHeaven.keyword.domain.notice.type.NoticeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeEvent {
  private NoticeType noticeType;
  private Long id;
  private Long memberId;
}
