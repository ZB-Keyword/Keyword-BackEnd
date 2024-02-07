package DevHeaven.keyword.domain.friend.dto.response;

import DevHeaven.keyword.domain.friend.type.FriendResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendSearchListResponse{

    private Long memberId;
    private String imageUrl;
    private String name;
    private String email;
    private FriendResponseStatus status;

}
