package DevHeaven.keyword.domain.friend.dto.request;

import DevHeaven.keyword.domain.friend.type.FriendStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendApproveRequest {

    private FriendStatus friendStatus;

}
