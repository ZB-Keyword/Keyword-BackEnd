package DevHeaven.keyword.config;

import DevHeaven.keyword.common.security.JwtAccessDeniedHandler;
import DevHeaven.keyword.common.security.JwtAuthenticationEntryPoint;
import DevHeaven.keyword.common.security.JwtAuthenticationFilter;
import DevHeaven.keyword.common.security.JwtExceptionFilter;
import DevHeaven.keyword.domain.member.service.oauth.Oauth2UserService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtExceptionFilter jwtExceptionFilter;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
  private final Oauth2UserService oauth2UserService;

  private static final String[] PERMIT_URL_PATTERNS = {
      "/members/signup",
      "/members/signin",
      "/members/reissue",
      "/login/oauth2/code/naver",
  };

  @Bean
  public SecurityFilterChain filterChain(final HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .httpBasic().disable()
        .formLogin().disable()

        .cors().disable()   // TODO : 배포 후 cors 설정 예정
        .csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        .and()
        .authorizeRequests()
        .antMatchers(PERMIT_URL_PATTERNS).permitAll()
        .anyRequest().authenticated()

        .and()
        .exceptionHandling()
        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
        .accessDeniedHandler(jwtAccessDeniedHandler)

        .and()
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class)

        //Naver 소셜 로그인 설정
        .oauth2Login()
        .userInfoEndpoint()
        .userService(oauth2UserService)
        .and()
        .defaultSuccessUrl("/login/success")
        .failureUrl("/login/failure");

    return httpSecurity.build();
  }
}
