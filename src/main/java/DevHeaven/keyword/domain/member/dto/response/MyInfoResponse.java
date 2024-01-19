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
public class MyInfoResponse {
  private long memberId;
  private String name;
  private String email;
  private String phone;
  private URL imageUrl;

  public static MyInfoResponse from(final Member member, final URL imageUrl) {
    return MyInfoResponse.builder()
        .memberId(member.getMemberId())
        .name(member.getName())
        .email(member.getEmail())
        .phone(member.getPhone())
        .imageUrl(imageUrl)
        .build();
  }
}
