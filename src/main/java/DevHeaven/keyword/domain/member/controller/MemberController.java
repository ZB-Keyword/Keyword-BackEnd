package DevHeaven.keyword.domain.member.controller;

import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.member.dto.request.ModifyPasswordRequest;
import DevHeaven.keyword.domain.member.dto.request.ReissueRequest;
import DevHeaven.keyword.domain.member.dto.request.SigninRequest;
import DevHeaven.keyword.domain.member.dto.request.SignupRequest;
import DevHeaven.keyword.domain.member.dto.response.MemberInfoResponse;
import DevHeaven.keyword.domain.member.dto.response.MyInfoResponse;
import DevHeaven.keyword.domain.member.dto.response.SignupResponse;
import DevHeaven.keyword.domain.member.service.MemberService;
import DevHeaven.keyword.domain.member.service.oauth.OAuth2UserService;
import DevHeaven.keyword.domain.member.dto.response.TokenAndInfoResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  private final OAuth2UserService oauth2UserService;

  @GetMapping("")
  public ResponseEntity<MyInfoResponse> getMyInfo(
      final @AuthenticationPrincipal MemberAdapter memberAdapter) {
    return ResponseEntity.ok(memberService.getMyInfo(memberAdapter));
  }

  @GetMapping("/{memberId}")
  public ResponseEntity<MemberInfoResponse> getMemberInfo(final @PathVariable Long memberId) {
    return ResponseEntity.ok(memberService.getMemberInfo(memberId));
  }

  @PostMapping("/signup")
  public ResponseEntity<SignupResponse> signup(
      final @Valid @RequestBody SignupRequest signupRequest) {
    return ResponseEntity.ok(memberService.signup(signupRequest));
  }

  @PostMapping("/signin")
  public ResponseEntity<TokenAndInfoResponse> signin(final @RequestBody SigninRequest signinRequest) {
    return ResponseEntity.ok(memberService.signin(signinRequest));
  }

  @PostMapping("/reissue")
  public ResponseEntity<TokenAndInfoResponse> reissue(final @RequestBody ReissueRequest reissueRequest) {
    return ResponseEntity.ok(memberService.reissue(reissueRequest));
  }

  @PatchMapping("/password")
  public ResponseEntity<Boolean> modifyPassword(
      final @AuthenticationPrincipal MemberAdapter memberAdapter,
      final @Valid @RequestBody ModifyPasswordRequest modifyPasswordRequest) {
    return ResponseEntity.ok(memberService.modifyPassword(memberAdapter, modifyPasswordRequest));
  }

  @PatchMapping("/profile-image")
  public ResponseEntity<Boolean> modifyProfileImage(
      final @AuthenticationPrincipal MemberAdapter memberAdapter,
      final @RequestPart(required = false) MultipartFile profileImage) {
    return ResponseEntity.ok(memberService.modifyProfileImage(memberAdapter, profileImage));
  }

  @DeleteMapping("")
  public ResponseEntity<Boolean> withdraw(final @AuthenticationPrincipal MemberAdapter memberAdapter) {
    return ResponseEntity.ok(memberService.withdraw(memberAdapter));
  }
}
