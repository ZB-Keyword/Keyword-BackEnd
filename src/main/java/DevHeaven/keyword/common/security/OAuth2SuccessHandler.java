package DevHeaven.keyword.common.security;

import DevHeaven.keyword.domain.member.dto.OAuthMemberAdapter;
import DevHeaven.keyword.domain.member.dto.response.TokenAndInfoResponse;
import DevHeaven.keyword.domain.member.service.MemberService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Component
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

  private final MemberService memberService;

  // private static final String HOME_URL = "http://localhost:8080/"; // - local 테스트 용도
  private static final String HOME_URL = "http://localhost:5173";    // - front local 테스트 용도
  // private static final String HOME_URL = "https://keyword2.store/";

  // private static final String REDIRECT_URL = "members/signin/oauth/";
  private static final String REDIRECT_URL = "/auth/redirect";        // redirect url 수정

  @Override
  public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
      final Authentication authentication) throws IOException, ServletException {

    OAuthMemberAdapter oAuthMemberAdapter = (OAuthMemberAdapter) authentication.getPrincipal();

    TokenAndInfoResponse tokenAndInfoResponse = memberService.signinOAuth(oAuthMemberAdapter);
    log.error("token 도착 ? = {}",tokenAndInfoResponse.getTokenResponse().getAccessToken());
    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(HOME_URL+REDIRECT_URL);
    // TODO : 현재는 Query String 형태로 프론트에 정보를 제공하지만, 추후 더 좋은 방법으로 교체 예정
    String redirectUri= uriComponentsBuilder
        .queryParam("accesstoken",tokenAndInfoResponse.getTokenResponse().getAccessToken())
        .queryParam("refreshtoken",tokenAndInfoResponse.getTokenResponse().getRefreshToken())
        .build()
        .toUriString();

    response.sendRedirect(redirectUri);

  }

  private String makeRedirectUrl(final TokenAndInfoResponse tokenAndInfoResponse) {
    return REDIRECT_URL + "?"
        + "accessToken=" + tokenAndInfoResponse.getTokenResponse().getAccessToken() + "&"
        + "refreshToken=" + tokenAndInfoResponse.getTokenResponse().getRefreshToken();
  }
}
