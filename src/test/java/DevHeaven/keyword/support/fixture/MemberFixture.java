package DevHeaven.keyword.support.fixture;

import DevHeaven.keyword.domain.member.entity.Member;

public enum MemberFixture {

  DOG(1L, "imgUrl", "강아지", "010-1234-1234","dog@gmail.com","123456d"),
  CAT(2L, "imgUrl", "고양이", "010-1234-1234","cat@gmail.com","123456d");
  private Long id;
  private String imageUrl;
  private String name;
  private String phone;
  private String email;
  private String password;
  //private MemberStatus status;

  MemberFixture(final Long id ,final String imageUrl ,final String name ,final String phone ,final String email ,
      final  String password) {
    this.id = id;
    this.imageUrl = imageUrl;
    this.name = name;
    this.phone = phone;
    this.email = email;
    this.password = password;
    //this.status = status;
  }

  public Member createMember(){
    return Member.builder()
        .id(this.id)
        .imageUrl(this.imageUrl)
        .name(this.name)
        .phone(this.phone)
        .email(this.email)
        .password(this.password)
        .build();
  }
}
