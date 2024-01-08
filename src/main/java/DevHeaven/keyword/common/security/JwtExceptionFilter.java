package DevHeaven.keyword.common.security;

import static DevHeaven.keyword.common.exception.type.ErrorCode.EXPIRED_JWT_EXCEPTION;
import static DevHeaven.keyword.common.exception.type.ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION;
import static DevHeaven.keyword.common.exception.type.ErrorCode.MALFORMED_JWT_EXCEPTION;
import static DevHeaven.keyword.common.exception.type.ErrorCode.SIGNATURE_EXCEPTION;
import static DevHeaven.keyword.common.exception.type.ErrorCode.UNSUPPORTED_JWT_EXCEPTION;

import DevHeaven.keyword.common.exception.JwtException;
import DevHeaven.keyword.common.exception.dto.ErrorResponse;
import DevHeaven.keyword.common.exception.type.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      filterChain.doFilter(request, response);
    } catch (JwtException e) {
      sendErrorResponse(response, e.getErrorCode());
    }
  }

  private void sendErrorResponse(HttpServletResponse response, final ErrorCode errorCode)
      throws IOException {
    response.setCharacterEncoding("UTF-8");
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(errorCode.getHttpStatus().value());
    response.getWriter().write(getJsonByErrorCode(errorCode));
  }

  private String getJsonByErrorCode(final ErrorCode errorCode) throws JsonProcessingException {
    return objectMapper.writeValueAsString(ErrorResponse.from(errorCode));
  }
}
