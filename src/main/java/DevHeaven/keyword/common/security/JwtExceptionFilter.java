package DevHeaven.keyword.common.security;

import static DevHeaven.keyword.common.exception.type.ErrorCode.EXPIRED_JWT_EXCEPTION;
import static DevHeaven.keyword.common.exception.type.ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION;
import static DevHeaven.keyword.common.exception.type.ErrorCode.MALFORMED_JWT_EXCEPTION;
import static DevHeaven.keyword.common.exception.type.ErrorCode.SIGNATURE_EXCEPTION;
import static DevHeaven.keyword.common.exception.type.ErrorCode.UNSUPPORTED_JWT_EXCEPTION;

import DevHeaven.keyword.common.exception.JwtException;
import DevHeaven.keyword.common.exception.dto.ErrorResponse;
import DevHeaven.keyword.common.exception.type.ErrorCode;
import DevHeaven.keyword.common.exception.util.ErrorResponseUtils;
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

  private final ErrorResponseUtils errorResponseUtils;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      filterChain.doFilter(request, response);
    } catch (JwtException e) {
      errorResponseUtils.sendErrorResponse(response, e.getErrorCode());
    }
  }
}
