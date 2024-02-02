package DevHeaven.keyword.domain.member.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MemberControllerTestType {

  GET_MY_INFO("내 정보 가져오기", "Member getMyInfo Response", ""),

  ;

  private final String summary;
  private final String schema;
  private final String url;

  public String getDescriptionFormatBySummary() {
    return this.summary + " : ";
  }
}
