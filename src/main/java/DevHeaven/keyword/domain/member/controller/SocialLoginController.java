package DevHeaven.keyword.domain.member.controller;

import DevHeaven.keyword.domain.member.dto.OAuthMemberAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SocialLoginController {

    @GetMapping("/social")
    public ResponseEntity<String> loginSuccess(final @AuthenticationPrincipal OAuthMemberAdapter OAuthMemberAdapter) {
        // 소셜 로그인 성공 후 로직을 추가하거나 세션에 사용자 정보를 저장하는 등의 작업을 수행
        // principalDetails에 사용자 정보가 담겨 있음
        String socialLoginMessage = "소셜 로그인 한 회원: " + OAuthMemberAdapter.getUsername();

        return ResponseEntity.ok(socialLoginMessage);
    }

    //authorization/naver 형식으로 적으면, yml(authorization-uri)에 등록된 주소로 이동
    @GetMapping("/oauth2/authorization/naver")
    public String login() {
        return "login";
    }
}
