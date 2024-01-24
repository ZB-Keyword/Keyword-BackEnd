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

  private static final String OAUTH_REDIRECT_SCHEME = "https";
  private static final String OAUTH_REDIRECT_HOST = "keyword-front-end.vercel.app";
  private static final String OAUTH_REDIRECT_PATH = "/auth/redirect";
  private static final String OAUTH_REDIRECT_ACCESS_TOKEN_QUERY_PARAM = "access-token";
  private static final String OAUTH_REDIRECT_REFRESH_TOKEN_QUERY_PARAM = "refresh-token";

  @Override
  public void onAuthenticationSuccess(final HttpServletRequest request,
      final HttpServletResponse response,
      final Authentication authentication) throws IOException, ServletException {

    log.info("success to social login");

    OAuthMemberAdapter oAuthMemberAdapter = (OAuthMemberAdapter) authentication.getPrincipal();

    TokenAndInfoResponse tokenAndInfoResponse = memberService.signinOAuth(oAuthMemberAdapter);

    // TODO : 현재는 Query String 형태로 프론트에 정보를 제공하지만, 추후 더 좋은 방법으로 교체 예정
    String redirectUri = getRedirectUrlByTokenAndInfoResponse(tokenAndInfoResponse);
    response.sendRedirect(redirectUri);
  }

  private String getRedirectUrlByTokenAndInfoResponse(
      final TokenAndInfoResponse tokenAndInfoResponse) {

    // http://localhost:8080/auth/redirect/{memberId}?access-token=..&refresh-token=..
    return UriComponentsBuilder.newInstance()
        .scheme(OAUTH_REDIRECT_SCHEME)
        .host(OAUTH_REDIRECT_HOST)
        .path(OAUTH_REDIRECT_PATH)
        .path("/" + tokenAndInfoResponse.getMyInfoResponse().getMemberId())
        .queryParam(OAUTH_REDIRECT_ACCESS_TOKEN_QUERY_PARAM,
            tokenAndInfoResponse.getTokenResponse().getAccessToken())
        .queryParam(OAUTH_REDIRECT_REFRESH_TOKEN_QUERY_PARAM,
            tokenAndInfoResponse.getTokenResponse().getRefreshToken())
        .toUriString();
  }
}
