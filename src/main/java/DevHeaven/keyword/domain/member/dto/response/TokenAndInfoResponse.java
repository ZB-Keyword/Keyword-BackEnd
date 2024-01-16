package DevHeaven.keyword.domain.member.dto.response;

import DevHeaven.keyword.common.security.dto.TokenResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenAndInfoResponse {
  private TokenResponse tokenResponse;
  private MyInfoResponse myInfoResponse;
}
