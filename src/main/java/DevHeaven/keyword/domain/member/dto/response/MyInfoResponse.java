package DevHeaven.keyword.domain.member.dto.response;

import DevHeaven.keyword.domain.member.entity.Member;
import java.net.URL;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class MyInfoResponse {
  private long memberId;
  private String name;
  private String email;
  private String phone;
  private String imageUrl;

  public static MyInfoResponse from(final Member member, final String imageUrl) {
    return MyInfoResponse.builder()
        .memberId(member.getMemberId())
        .name(member.getName())
        .email(member.getEmail())
        .phone(member.getPhone())
        .imageUrl(imageUrl)
        .build();
  }
}
