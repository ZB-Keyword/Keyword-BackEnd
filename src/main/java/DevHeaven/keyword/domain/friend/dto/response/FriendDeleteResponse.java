package DevHeaven.keyword.domain.friend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FriendDeleteResponse {

  @Builder.Default
  private Boolean isFriendDelete = false;
}
