package DevHeaven.keyword.common.security;

import static DevHeaven.keyword.common.exception.type.ErrorCode.ACCESS_DENIED_JWT_EXCEPTION;

import DevHeaven.keyword.common.exception.util.ErrorResponseUtils;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

  private final ErrorResponseUtils errorResponseUtils;

  @Override
  public void handle(final HttpServletRequest request, final HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {
    errorResponseUtils.sendErrorResponse(response, ACCESS_DENIED_JWT_EXCEPTION);
  }
}
