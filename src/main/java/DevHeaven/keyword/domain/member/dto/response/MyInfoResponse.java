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
public class MyInfoResponse {
  private String name;
  private String email;
  private String phone;
  private String imageUrl;

  public static MyInfoResponse from(Member member) {
    return MyInfoResponse.builder()
        .name(member.getName())
        .email(member.getEmail())
        .phone(member.getPhone())
        .imageUrl(member.getProfileImageFileName())
        .build();
  }
}
