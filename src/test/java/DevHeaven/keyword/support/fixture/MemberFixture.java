package DevHeaven.keyword.support.fixture;

import static DevHeaven.keyword.domain.member.type.MemberProvider.KEYWORD;
import static DevHeaven.keyword.domain.member.type.MemberProvider.NAVER;
import static DevHeaven.keyword.domain.member.type.MemberRole.MEMBER;
import static DevHeaven.keyword.domain.member.type.MemberStatus.ACTIVE;
import static DevHeaven.keyword.domain.member.type.MemberStatus.BLOCKED;

import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.type.MemberProvider;
import DevHeaven.keyword.domain.member.type.MemberRole;
import DevHeaven.keyword.domain.member.type.MemberStatus;
import lombok.Getter;

@Getter
public enum MemberFixture {

  DOG(1L, "imgUrl", "강아지", "010-1234-1234", "dog@gmail.com", "123456abc!", ACTIVE, MEMBER, KEYWORD),
  CAT(2L, "imgUrl", "고양이", "010-1234-1234", "cat@gmail.com", "123456abc!", ACTIVE, MEMBER, NAVER),
  DUCK(3L, "imgUrl", null, "031031", "duck#gmail.com", "wrongPasswordRegex", ACTIVE, MEMBER, KEYWORD),
  BAD_DUCK(4L, "imgUrl", "나쁜 오리", "010-1234-1234", "badduck@gmail.com", "123456abc!", BLOCKED, MEMBER, KEYWORD),
  ;

  private Long memberId;
  private String imageUrl;
  private String name;
  private String phone;
  private String email;
  private String password;
  private MemberStatus status;
  private MemberRole role;
  private MemberProvider provider;

  MemberFixture(final Long memberId, final String imageUrl, final String name, final String phone,
      final String email, final String password, MemberStatus status, final MemberRole role, final MemberProvider provider) {
    this.memberId = memberId;
    this.imageUrl = imageUrl;
    this.name = name;
    this.phone = phone;
    this.email = email;
    this.password = password;
    this.status = status;
    this.role = role;
    this.provider = provider;
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
        .provider(this.provider)
        .build();
  }
}
