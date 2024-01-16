package DevHeaven.keyword.domain.member.controller;

import DevHeaven.keyword.common.security.dto.TokenResponse;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.member.dto.request.SigninRequest;
import DevHeaven.keyword.domain.member.dto.request.SignupRequest;
import DevHeaven.keyword.domain.member.dto.response.MemberInfoResponse;
import DevHeaven.keyword.domain.member.dto.response.MyInfoResponse;
import DevHeaven.keyword.domain.member.dto.response.SignupResponse;
import DevHeaven.keyword.domain.member.service.MemberService;
import DevHeaven.keyword.domain.member.dto.PrincipalDetails;
import DevHeaven.keyword.domain.member.service.oauth.Oauth2UserService;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  private final Oauth2UserService oauth2UserService;

  @GetMapping("")
  public ResponseEntity<MyInfoResponse> getMyInfo(final @AuthenticationPrincipal MemberAdapter memberAdapter) {
    return ResponseEntity.ok(memberService.getMyInfo(memberAdapter));
  }

  @GetMapping("/{memberId}")
  public ResponseEntity<MemberInfoResponse> getMemberInfo(final @PathVariable Long memberId) {
    return ResponseEntity.ok(memberService.getMemberInfo(memberId));
  }

  @PostMapping("/signup")
  public ResponseEntity<SignupResponse> signup(final @Valid @RequestBody SignupRequest signupRequest) {
    return ResponseEntity.ok(memberService.signup(signupRequest));
  }

  @PostMapping("/signin")
  public ResponseEntity<TokenResponse> signin(final @RequestBody SigninRequest signinRequest) {
    return ResponseEntity.ok(memberService.signin(signinRequest));
  }
}
