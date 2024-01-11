package DevHeaven.keyword.domain.member.dto.response;

import DevHeaven.keyword.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoResponse {
  private String name;
  private String email;
  private String imageUrl;

  public static MemberInfoResponse from(Member member) {
    return MemberInfoResponse.builder()
        .name(member.getName())
        .email(member.getEmail())
        .imageUrl(member.getImageUrl())
        .build();
  }
}
