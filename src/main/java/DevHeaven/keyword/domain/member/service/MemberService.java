package DevHeaven.keyword.domain.member.service;

import static DevHeaven.keyword.common.exception.type.ErrorCode.ALREADY_EXISTS_EMAIL;
import static DevHeaven.keyword.common.exception.type.ErrorCode.BLOCKED_MEMBER;
import static DevHeaven.keyword.common.exception.type.ErrorCode.EMAIL_NOT_FOUND;
import static DevHeaven.keyword.common.exception.type.ErrorCode.INACTIVE_MEMBER;
import static DevHeaven.keyword.common.exception.type.ErrorCode.MEMBER_NOT_FOUND;
import static DevHeaven.keyword.common.exception.type.ErrorCode.MISMATCH_PASSWORD;
import static DevHeaven.keyword.common.exception.type.ErrorCode.WITHDRAWN_MEMBER;
import static DevHeaven.keyword.domain.member.type.MemberRole.MEMBER;
import static DevHeaven.keyword.domain.member.type.MemberStatus.ACTIVE;

import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.common.security.JwtUtils;
import DevHeaven.keyword.common.security.dto.TokenResponse;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.member.dto.request.SigninRequest;
import DevHeaven.keyword.domain.member.dto.request.SignupRequest;
import DevHeaven.keyword.domain.member.dto.response.MemberInfoResponse;
import DevHeaven.keyword.domain.member.dto.response.MyInfoResponse;
import DevHeaven.keyword.domain.member.dto.response.SignupResponse;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import DevHeaven.keyword.domain.member.type.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;

  private final JwtUtils jwtUtils;

  private final PasswordEncoder passwordEncoder;

  public MyInfoResponse getMyInfo(final MemberAdapter memberAdapter) {
    Member member = getMemberByEmail(memberAdapter.getEmail());

    validateMemberByStatus(member);

    return MyInfoResponse.from(member);
  }

  public MemberInfoResponse getMemberInfo(final Long memberId) {
    Member member = getMemberById(memberId);

    validateMemberByStatus(member);

    return MemberInfoResponse.from(member);
  }

  public SignupResponse signup(final SignupRequest signupRequest) {
    validateMemberByEmail(signupRequest.getEmail());

    Member member = memberRepository.save(
        Member.builder()
            .name(signupRequest.getName())
            .phone(signupRequest.getPhone())
            .email(signupRequest.getEmail())
            .password(passwordEncoder.encode(signupRequest.getPassword()))
            .status(ACTIVE)   // 추후 이메일 인증이 구현되면 수정 예정
            .role(MEMBER)   // 추후 role 이 추가되면 수정 예정
            .build()
    );

    return new SignupResponse(member.getName());
  }

  public TokenResponse signin(final SigninRequest signinRequest) {
    Member member = getMemberByEmail(signinRequest.getEmail());

    validateMemberByPassword(signinRequest.getPassword(), member);

    validateMemberByStatus(member);
    
    // TODO : refresh token 은 redis 설정 merge 후 저장 예정 (임시 response)

    return jwtUtils.createTokens(member.getEmail());
  }


  // validate method

  private Member getMemberById(final long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
  }

  private Member getMemberByEmail(final String email) {
     return memberRepository.findByEmail(email)
        .orElseThrow(() -> new MemberException(EMAIL_NOT_FOUND));
  }

  private void validateMemberByEmail(final String email) {
    if(memberRepository.existsByEmail(email)) {
      throw new MemberException(ALREADY_EXISTS_EMAIL);
    }
  }

  private void validateMemberByPassword(final String targetPassword, final Member member) {
    if(!passwordEncoder.matches(targetPassword, member.getPassword())) {
      throw new MemberException(MISMATCH_PASSWORD);
    }
  }

  private void validateMemberByStatus(final Member member) {
    switch (member.getStatus()) {
      case BLOCKED:
        throw new MemberException(BLOCKED_MEMBER);
      case INACTIVE:
        throw new MemberException(INACTIVE_MEMBER);
      case WITHDRAWN:
        throw new MemberException(WITHDRAWN_MEMBER);
    }
  }
}
