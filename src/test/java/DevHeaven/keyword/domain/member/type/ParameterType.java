package DevHeaven.keyword.domain.member.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ParameterType {

  MEMBER_ID(Long.class, "memberId", "대상이 되는 회원의 id (pk)"),

  ;

  private Class<?> type;
  private String parameterName;
  private String description;
}
