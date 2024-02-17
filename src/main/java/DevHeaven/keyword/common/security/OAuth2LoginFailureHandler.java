package DevHeaven.keyword.common.security;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {
  private static final String HOME_URL = "http://localhost:5173";    // - front local 테스트 용도
  private static final String REDIRECT_URL = "/auth/redirect";        // redirect url 수정

  @Override
  public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response,
      final AuthenticationException exception)
      throws IOException {
    final UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(HOME_URL+REDIRECT_URL);
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    response.sendRedirect(uriComponentsBuilder.build().toString());
  }
}
