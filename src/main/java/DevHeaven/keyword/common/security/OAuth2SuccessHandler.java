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


    final OAuthMemberAdapter oAuthMemberAdapter = (OAuthMemberAdapter) authentication.getPrincipal();

    final TokenAndInfoResponse tokenAndInfoResponse = memberService.signinOAuth(oAuthMemberAdapter);

    final String redirectUri = getRedirectUrlByTokenAndInfoResponse(tokenAndInfoResponse);
    response.sendRedirect(redirectUri);
  }

  private String getRedirectUrlByTokenAndInfoResponse(
      final TokenAndInfoResponse tokenAndInfoResponse) {
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
