package DevHeaven.keyword.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DomainType {

  MEMBER("/members"),

  ;

  private String prefixUrl;
}
