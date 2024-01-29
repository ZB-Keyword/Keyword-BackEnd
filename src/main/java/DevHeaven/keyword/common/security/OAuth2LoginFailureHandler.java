package DevHeaven.keyword.common.security;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

  private static final String HOME_URL = "http://localhost:5173";    // - front local 테스트 용도
  // private static final String HOME_URL = "https://keyword2.store/";

  // private static final String REDIRECT_URL = "members/signin/oauth/";
  private static final String REDIRECT_URL = "/auth/redirect";        // redirect url 수정

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException {
    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(HOME_URL+REDIRECT_URL);
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    response.sendRedirect(uriComponentsBuilder.build().toString());
    log.info("소셜 로그인에 실패했습니다. 에러 메시지 : {}", exception.getMessage());
    log.error("소셜 실패 : {}", exception.getMessage());
  }
}
