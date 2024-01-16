package DevHeaven.keyword.domain.schedule.dto.request;

import DevHeaven.keyword.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ScheduleFriendRequest {
    private Long memberId;
    private String name;

}
