package DevHeaven.keyword.domain.friend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendDeleteResponse {

  @Builder.Default
  private Boolean isFriendDelete = false;
}
