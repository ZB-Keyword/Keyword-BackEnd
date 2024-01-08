package DevHeaven.keyword.common.security;

import static DevHeaven.keyword.common.exception.type.ErrorCode.EXPIRED_JWT_EXCEPTION;
import static DevHeaven.keyword.common.exception.type.ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION;
import static DevHeaven.keyword.common.exception.type.ErrorCode.MALFORMED_JWT_EXCEPTION;
import static DevHeaven.keyword.common.exception.type.ErrorCode.SIGNATURE_EXCEPTION;
import static DevHeaven.keyword.common.exception.type.ErrorCode.UNSUPPORTED_JWT_EXCEPTION;

import DevHeaven.keyword.common.exception.JwtException;
import DevHeaven.keyword.common.security.dto.TokenResponse;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.service.MemberDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtils {

  private static final String TOKEN_HEADER = "Authorization";
  private static final String TOKEN_PREFIX = "Bearer ";

  private static final long ACCESS_TOKEN_VALID_TIME = 3 * 60 * 60 * 1000L;  // 3 hours
  private static final long REFRESH_TOKEN_VALID_TIME = 7 * 24 * 60 * 60 * 1000L; // 7 days

  private final MemberDetailsService memberDetailsService;

  @Value("${spring.jwt.secret}")
  private String secretKey;
  private Key key;

  @PostConstruct
  protected void init() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    key = Keys.hmacShaKeyFor(keyBytes);
  }

  public TokenResponse createTokens(String email) {
    Date tokenCreatedDate = new Date();

    Claims claims = Jwts.claims();
    claims.setSubject(email);

    long accessTokenExpiredDate = tokenCreatedDate.getTime() + ACCESS_TOKEN_VALID_TIME;
    long refreshTokenExpiredDate = tokenCreatedDate.getTime() + REFRESH_TOKEN_VALID_TIME;

    String accessToken = Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(tokenCreatedDate)
        .setExpiration(new Date(accessTokenExpiredDate))
        .signWith(key)
        .compact();

    String refreshToken = Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(tokenCreatedDate)
        .setExpiration(new Date(refreshTokenExpiredDate))
        .signWith(key)
        .compact();

    return TokenResponse.builder()
        .grantType(TOKEN_PREFIX.substring(0, TOKEN_PREFIX.length() - 1))
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .accessTokenExpiredDate(accessTokenExpiredDate)
        .build();
  }

  public String extractTokenByRequest(HttpServletRequest request) {
    String tokenHeader = request.getHeader(TOKEN_HEADER);

    if (!StringUtils.hasLength(tokenHeader) || !tokenHeader.startsWith(TOKEN_PREFIX)) {
      return null;
    }

    return tokenHeader.substring(TOKEN_PREFIX.length());
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  public Claims getClaimsByToken(String token) {
    try {
      return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    } catch (ExpiredJwtException e) {
      throw new JwtException(EXPIRED_JWT_EXCEPTION);
    } catch (UnsupportedJwtException e) {
      throw new JwtException(UNSUPPORTED_JWT_EXCEPTION);
    } catch (MalformedJwtException e) {
      throw new JwtException(MALFORMED_JWT_EXCEPTION);
    } catch (SignatureException e) {
      throw new JwtException(SIGNATURE_EXCEPTION);
    } catch (IllegalArgumentException e) {
      throw new JwtException(ILLEGAL_ARGUMENT_EXCEPTION);
    }
  }

  public boolean validateToken(String token) {
    if(!StringUtils.hasText(token)) {
      return false;
    }

    return !getClaimsByToken(token).getExpiration().before(new Date());
  }

  public Authentication getAuthenticationByToken(String token) {
    UserDetails userDetails = memberDetailsService.loadUserByUsername(getClaimsByToken(token).getSubject());
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }
}
