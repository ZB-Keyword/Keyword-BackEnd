package DevHeaven.keyword.common.security;

import static DevHeaven.keyword.common.exception.type.ErrorCode.UNAUTHENTICATED_JWT_EXCEPTION;

import DevHeaven.keyword.common.exception.util.ErrorResponseUtils;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ErrorResponseUtils errorResponseUtils;

  @Override
  public void commence(final HttpServletRequest request, final HttpServletResponse response,
      final AuthenticationException authException) throws IOException, ServletException {
    errorResponseUtils.sendErrorResponse(response, UNAUTHENTICATED_JWT_EXCEPTION);
  }
}
