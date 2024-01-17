package DevHeaven.keyword.domain.friend.dto.response;

import DevHeaven.keyword.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendListResponse {

  private Long memberId;
  private String name;
  private String email;
  private String profileImageUrl;

  public static FriendListResponse from(Member friend){
    return FriendListResponse.builder()
        .memberId(friend.getMemberId())
        .name(friend.getName())
        .email(friend.getEmail())
        .build();
  }

  public void modifyImageUrl(String imageUrl){
    this.profileImageUrl = imageUrl;
  }
}
