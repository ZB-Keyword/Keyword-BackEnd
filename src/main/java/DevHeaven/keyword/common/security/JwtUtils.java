package DevHeaven.keyword.common.security;

import DevHeaven.keyword.common.security.dto.TokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

public class JwtUtils {

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private static final long ACCESS_TOKEN_VALID_TIME = 3 * 60 * 60 * 1000L;  // 3 hours
    private static final long REFRESH_TOKEN_VALID_TIME = 7 * 24 * 60 * 60 * 1000L; // 7 days

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
                .grantType(TOKEN_PREFIX.substring(0, TOKEN_PREFIX.length()-1))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiredDate(accessTokenExpiredDate)
                .build();
    }

    public String extractTokenByRequest(HttpServletRequest request) {
        String tokenHeader = request.getHeader(TOKEN_HEADER);

        if(!StringUtils.hasLength(tokenHeader) || !tokenHeader.startsWith(TOKEN_PREFIX)) {
            return null;
        }

        return tokenHeader.substring(TOKEN_PREFIX.length());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
