package DevHeaven.keyword.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/oauth2/authorization")
public class OAuthController {

  @GetMapping("/naver")
  public ResponseEntity<Boolean> naverSocialLogin() {
    return ResponseEntity.ok(true);
  }
}
