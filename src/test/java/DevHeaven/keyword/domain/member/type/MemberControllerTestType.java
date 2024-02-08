package DevHeaven.keyword.domain.member.type;

import static DevHeaven.keyword.type.DomainType.MEMBER;

import DevHeaven.keyword.type.DomainType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MemberControllerTestType {

  GET_MY_INFO("내 정보 가져오기", "Member getMyInfo Response", ""),
  GET_MEMBER_INFO("다른 회원 정보 가져오기", "Member getMemberInfo Response", "/{memberId}"),
  SIGNUP("회원 가입", "Member signup Response", "/signup"),
  SIGNIN("로그인", "Member signin Response", "/signin"),

  ;

  private final String summary;
  private final String schema;
  private final String url;

  private static final DomainType domain = MEMBER;
  private static final String failMessage = " 실패 시";
  private static final String requestMessage = " 요청 시";

  public String getSuccessResponseDescriptionFormatBySummary() {
    return this.summary + " : ";
  }

  public String getFailResponseDescriptionFormatBySummary() {
    return this.summary + failMessage + " : ";
  }

  public String getRequestDescriptionFormatBySummary() {
    return this.summary + requestMessage + " : ";
  }

  public String getFullUrl() {
    return domain.getPrefixUrl() + this.getUrl();
  }
}
