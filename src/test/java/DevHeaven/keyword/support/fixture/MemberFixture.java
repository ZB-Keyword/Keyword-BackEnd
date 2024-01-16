package DevHeaven.keyword.support.fixture;

import static DevHeaven.keyword.domain.member.type.MemberRole.MEMBER;
import static DevHeaven.keyword.domain.member.type.MemberStatus.ACTIVE;

import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.type.MemberRole;
import DevHeaven.keyword.domain.member.type.MemberStatus;

public enum MemberFixture {

  DOG(1L, "imgUrl", "강아지", "010-1234-1234", "dog@gmail.com", "123456d", ACTIVE, MEMBER),
  CAT(2L, "imgUrl", "고양이", "010-1234-1234", "cat@gmail.com", "123456d", ACTIVE, MEMBER);
  private Long memberId;
  private String imageUrl;
  private String name;
  private String phone;
  private String email;
  private String password;
  private MemberStatus status;
  private MemberRole role;

  MemberFixture(final Long memberId, final String imageUrl, final String name, final String phone,
      final String email, final String password, MemberStatus status, final MemberRole role) {
    this.memberId = memberId;
    this.imageUrl = imageUrl;
    this.name = name;
    this.phone = phone;
    this.email = email;
    this.password = password;
    this.status = status;
    this.role = role;
  }

  public Member createMember() {
    return Member.builder()
        .memberId(this.memberId)
        .profileImageFileName(this.imageUrl)
        .name(this.name)
        .phone(this.phone)
        .email(this.email)
        .password(this.password)
        .status(this.status)
        .role(this.role)
        .build();
  }
}
