package DevHeaven.keyword.domain.member.dto.response;

import DevHeaven.keyword.domain.member.entity.Member;
import java.net.URL;
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
  private URL imageUrl;

  public static MemberInfoResponse from(Member member, URL imageUrl) {
    return MemberInfoResponse.builder()
        .name(member.getName())
        .email(member.getEmail())
        .imageUrl(imageUrl)
        .build();
  }
}
