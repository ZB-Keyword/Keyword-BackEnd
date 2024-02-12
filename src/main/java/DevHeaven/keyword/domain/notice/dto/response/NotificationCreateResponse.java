package DevHeaven.keyword.domain.notice.dto.response;

import DevHeaven.keyword.domain.notice.type.NoticeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationCreateResponse {

    private Long noticeId;
    private NoticeType type;
    private String message;

}
