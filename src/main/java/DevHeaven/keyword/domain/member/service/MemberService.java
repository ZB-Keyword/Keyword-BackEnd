package DevHeaven.keyword.domain.member.service;

import static DevHeaven.keyword.common.exception.type.ErrorCode.ALREADY_EXISTS_EMAIL;
import static DevHeaven.keyword.common.exception.type.ErrorCode.ALREADY_EXISTS_PHONE;
import static DevHeaven.keyword.common.exception.type.ErrorCode.BLOCKED_MEMBER;
import static DevHeaven.keyword.common.exception.type.ErrorCode.EMAIL_NOT_FOUND;
import static DevHeaven.keyword.common.exception.type.ErrorCode.INACTIVE_MEMBER;
import static DevHeaven.keyword.common.exception.type.ErrorCode.MEMBER_NOT_FOUND;
import static DevHeaven.keyword.common.exception.type.ErrorCode.MISMATCH_PASSWORD;
import static DevHeaven.keyword.common.exception.type.ErrorCode.MISMATCH_OAUTH_PROVIDER;
import static DevHeaven.keyword.common.exception.type.ErrorCode.MISMATCH_SIGN_IN_PROVIDER;
import static DevHeaven.keyword.common.exception.type.ErrorCode.REFRESH_TOKEN_EXPIRED;
import static DevHeaven.keyword.common.exception.type.ErrorCode.REFRESH_TOKEN_NOT_FOUND;
import static DevHeaven.keyword.common.exception.type.ErrorCode.WITHDRAWN_MEMBER;
import static DevHeaven.keyword.domain.member.type.MemberProvider.KEYWORD;
import static DevHeaven.keyword.domain.member.type.MemberRole.MEMBER;
import static DevHeaven.keyword.domain.member.type.MemberStatus.ACTIVE;
import static DevHeaven.keyword.domain.member.type.MemberStatus.WITHDRAWN;

import DevHeaven.keyword.common.exception.JwtException;
import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.common.security.JwtUtils;
import DevHeaven.keyword.common.security.dto.TokenResponse;
import DevHeaven.keyword.common.service.image.AmazonS3FileService;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.member.dto.OAuthMemberAdapter;
import DevHeaven.keyword.domain.member.dto.request.ModifyPasswordRequest;
import DevHeaven.keyword.domain.member.dto.request.ReissueRequest;
import DevHeaven.keyword.domain.member.dto.request.SigninRequest;
import DevHeaven.keyword.domain.member.dto.request.SignupRequest;
import DevHeaven.keyword.domain.member.dto.response.MemberInfoResponse;
import DevHeaven.keyword.domain.member.dto.response.MyInfoResponse;
import DevHeaven.keyword.domain.member.dto.response.SignupResponse;
import DevHeaven.keyword.domain.member.dto.response.TokenAndInfoResponse;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import DevHeaven.keyword.domain.member.type.MemberProvider;
import DevHeaven.keyword.domain.member.type.MemberStatus;
import java.net.URL;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@ToString
public class MemberService {

  private static final String REDIS_REFRESH_TOKEN_KEY_PREFIX = "reissueMemberEmail::";

  private final MemberRepository memberRepository;
  private final RedisTemplate<String, Object> redisTemplate;

  private final AmazonS3FileService amazonS3FileService;

  private final JwtUtils jwtUtils;

  private final PasswordEncoder passwordEncoder;


  public MyInfoResponse getMyInfo(final MemberAdapter memberAdapter) {
    Member member = getMemberByEmail(memberAdapter.getEmail());

    validateMemberByStatus(member.getStatus());

    return MyInfoResponse.from(member, getURLByFileName(member.getProfileImageFileName()));
  }

  public MemberInfoResponse getMemberInfo(final Long memberId) {
    Member member = getMemberById(memberId);

    validateMemberByStatus(member.getStatus());

    return MemberInfoResponse.from(member, getURLByFileName(member.getProfileImageFileName()));
  }

  public SignupResponse signup(final SignupRequest signupRequest) {
    validateMemberByEmail(signupRequest.getEmail());

    validateMemberByPhone(signupRequest.getPhone());

    Member member = memberRepository.save(
        Member.builder()
            .name(signupRequest.getName())
            .phone(signupRequest.getPhone())
            .email(signupRequest.getEmail())
            .password(passwordEncoder.encode(signupRequest.getPassword()))
            .status(ACTIVE)   // 추후 이메일 인증이 구현되면 수정 예정
            .role(MEMBER)   // 추후 role 이 추가되면 수정 예정
            .provider(KEYWORD)
            .build()
    );

    return new SignupResponse(member.getName());
  }

  public TokenAndInfoResponse signin(final SigninRequest signinRequest) {
    Member member = getMemberByEmail(signinRequest.getEmail());

    validateMatchesSigninMemberProvider(member.getProvider());

    validateMemberByPassword(signinRequest.getPassword(), member);

    validateMemberByStatus(member.getStatus());

    TokenResponse tokenResponse = jwtUtils.createTokens(member.getEmail());

    String redisRefreshTokenKey = getRedisRefreshTokenKeyByMemberEmail(member.getEmail());

    saveRefreshTokenInRedisByKey(redisRefreshTokenKey, tokenResponse.getRefreshToken());

    return TokenAndInfoResponse.builder()
        .tokenResponse(tokenResponse)
        .myInfoResponse(MyInfoResponse.from(member, getURLByFileName(member.getProfileImageFileName())))
        .build();
  }

  public TokenAndInfoResponse signinOAuth(final OAuthMemberAdapter oAuthMemberAdapter) {
    Member member = getMemberByEmail(oAuthMemberAdapter.getEmail());

    validateMatchesOAuthMemberProvider(member.getProvider());

    return TokenAndInfoResponse.builder()
        .tokenResponse(jwtUtils.createTokens(oAuthMemberAdapter.getEmail()))
        .myInfoResponse(MyInfoResponse.from(member, getURLByFileName(member.getProfileImageFileName())))
        .build();
  }

  public TokenAndInfoResponse reissue(final ReissueRequest reissueRequest) {
    String reissueRequestMemberEmail = jwtUtils.getClaimsByToken(reissueRequest.getRefreshToken())
        .getSubject();

    Member member = getMemberByEmail(reissueRequestMemberEmail);

    TokenResponse tokenResponse = jwtUtils.createTokens(
        member.getEmail());

    String redisRefreshTokenKey = getRedisRefreshTokenKeyByMemberEmail(reissueRequestMemberEmail);

    validateRefreshTokenInRedisByKey(redisRefreshTokenKey);

    saveRefreshTokenInRedisByKey(redisRefreshTokenKey, tokenResponse.getRefreshToken());

    return TokenAndInfoResponse.builder()
        .tokenResponse(tokenResponse)
        .myInfoResponse(MyInfoResponse.from(member, getURLByFileName(member.getProfileImageFileName())))
        .build();
  }

  public Boolean modifyPassword(final MemberAdapter memberAdapter,
      final ModifyPasswordRequest modifyPasswordRequest) {
    Member member = getMemberByEmail(memberAdapter.getEmail());

    validateMemberByStatus(member.getStatus());

    memberRepository.save(member.modifyPassword(
        passwordEncoder.encode(modifyPasswordRequest.getPassword())));

    return true;
  }

  public Boolean modifyProfileImage(final MemberAdapter memberAdapter,
      final MultipartFile requestedProfileImage) {
    Member member = getMemberByEmail(memberAdapter.getEmail());

    validateMemberByStatus(member.getStatus());

    String memberProfileImageFileName = member.getProfileImageFileName();

    if (memberProfileImageFileName != null) {
      amazonS3FileService.deleteFile(memberProfileImageFileName);
      member.modifyProfileImageFileName(null);
    }

    if (requestedProfileImage != null) {
      member.modifyProfileImageFileName(amazonS3FileService.saveImage(requestedProfileImage));
    }

    memberRepository.save(member);

    return true;
  }

  public Boolean withdraw(final MemberAdapter memberAdapter) {
    Member member = getMemberByEmail(memberAdapter.getEmail());

    validateMemberByStatus(member.getStatus());

    memberRepository.save(member.modifyStatus(WITHDRAWN));

    return true;
  }

  // private method

  private Member getMemberById(final long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
  }

  private Member getMemberByEmail(final String email) {
    return memberRepository.findByEmail(email)
        .orElseThrow(() -> new MemberException(EMAIL_NOT_FOUND));
  }

  private String getRedisRefreshTokenKeyByMemberEmail(final String email) {
    return REDIS_REFRESH_TOKEN_KEY_PREFIX + email;
  }

  private URL getURLByFileName(String fileName) {
    if(fileName == null) {
      return null;
    }

    return amazonS3FileService.createUrl(fileName);
  }

  private void validateMemberByEmail(final String email) {
    if (memberRepository.existsByEmail(email)) {
      throw new MemberException(ALREADY_EXISTS_EMAIL);
    }
  }

  private void validateMemberByPassword(final String targetPassword, final Member member) {
    if (!passwordEncoder.matches(targetPassword, member.getPassword())) {
      throw new MemberException(MISMATCH_PASSWORD);
    }
  }

  public void validateMemberByStatus(final MemberStatus status) {
    switch (status) {
      case BLOCKED:
        throw new MemberException(BLOCKED_MEMBER);
      case INACTIVE:
        throw new MemberException(INACTIVE_MEMBER);
      case WITHDRAWN:
        throw new MemberException(WITHDRAWN_MEMBER);
    }
  }

  private String validateRefreshTokenInRedisByKey(final String redisRefreshTokenKey) {
    String refreshToken = String.valueOf(redisTemplate.opsForValue().get(redisRefreshTokenKey));

    if (refreshToken == null) {
      throw new JwtException(REFRESH_TOKEN_NOT_FOUND);
    }

    if (!jwtUtils.validateToken(refreshToken)) {
      throw new JwtException(REFRESH_TOKEN_EXPIRED);
    }

    return refreshToken;
  }

  private void validateMemberByPhone(final String phone) {
    if (memberRepository.existsByPhone(phone)) {
      throw new MemberException(ALREADY_EXISTS_PHONE);
    }
  }

  private void validateMatchesSigninMemberProvider(final MemberProvider provider) {
    if(provider == null) {
      throw new MemberException(MEMBER_NOT_FOUND);
    }

    if (provider != KEYWORD) {
      throw new MemberException(MISMATCH_OAUTH_PROVIDER);
    }
  }

  private void validateMatchesOAuthMemberProvider(final MemberProvider provider) {
    if(provider == null) {
      throw new MemberException(MEMBER_NOT_FOUND);
    }

    if(provider == KEYWORD) {
      throw new MemberException(MISMATCH_SIGN_IN_PROVIDER);
    }
  }

  private void saveRefreshTokenInRedisByKey(final String redisRefreshTokenKey,
      final String refreshToken) {
    redisTemplate.opsForValue().set(redisRefreshTokenKey, refreshToken,
        Duration.ofMillis(jwtUtils.getRefreshTokenValidTime()));
  }
}
