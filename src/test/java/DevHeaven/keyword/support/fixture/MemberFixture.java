package DevHeaven.keyword.support.fixture;

import static DevHeaven.keyword.domain.member.type.MemberRole.MEMBER;
import static DevHeaven.keyword.domain.member.type.MemberStatus.ACTIVE;

import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.type.MemberRole;
import DevHeaven.keyword.domain.member.type.MemberStatus;

public enum MemberFixture {

  DOG(1L, "imgUrl", "강아지", "010-1524-1234", "dog@gmail.com", "123456d", ACTIVE, MEMBER),
  CAT(2L, "imgUrl", "고양이", "010-1414-1234", "cat@gmail.com", "123456d", ACTIVE, MEMBER),
  PANDA(3L, "imgUrl", "판다", "010-1154-1234", "panda@gmail.com", "123456d", ACTIVE, MEMBER),
  LION(4L, "imgUrl", "라이언양이", "010-1514-1234", "lion@gmail.com", "123456d", ACTIVE, MEMBER),
  RABBIT(5L, "imgUrl", "토끼", "010-1454-1234", "rabbit@gmail.com", "123456d", ACTIVE, MEMBER);;

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
