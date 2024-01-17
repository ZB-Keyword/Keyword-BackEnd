package DevHeaven.keyword.domain.member.service.oauth;

import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import DevHeaven.keyword.domain.member.dto.OAuthMemberAdapter;
import DevHeaven.keyword.domain.member.service.oauth.provider.NaverUserInfo;
import DevHeaven.keyword.domain.member.service.oauth.provider.OAuth2UserInfo;
import DevHeaven.keyword.domain.member.type.MemberProviderType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

import static DevHeaven.keyword.common.exception.type.ErrorCode.OAUTH_PROVIDER_NOT_VALID_EXCEPTION;
import static DevHeaven.keyword.domain.member.type.MemberRole.MEMBER;
import static DevHeaven.keyword.domain.member.type.MemberStatus.ACTIVE;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  // 네이버로 부터 받은 userRequest 데이터에 대한 후 처리 함수
  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

    // 네이버 로그인 창 -> 로그인 완료 -> code 리턴(Oauth-client 라이브러리) -> AccessToken 요청
    // userRequest 정보 -> loadUser 함수 호출 -> Oauth2(네이버)로 부터 정보 받아옴.
    OAuth2User oAuth2User = super.loadUser(userRequest);

    // OAuth2User의 attribute
    Map<String, Object> attributes = oAuth2User.getAttributes();

    // 사용자 정보를 제공하는 서비스 제공자 (Google, Naver 등)를 식별하기 위한 값
    ClientRegistration clientRegistration = userRequest.getClientRegistration();
    String clientId = clientRegistration.getClientId();
    String registrationId = clientRegistration.getRegistrationId();

    // 우리 서비스에서 제공하는 종류의 소셜 로그인인지 체크
    MemberProviderType provider = null;
    try {
      provider = Enum.valueOf(MemberProviderType.class, registrationId.toUpperCase());
    } catch (Exception e) {
      throw new MemberException(OAUTH_PROVIDER_NOT_VALID_EXCEPTION);
    }

    // Oauth2 사용자 정보 처리(Google, KaKao API 추가 시 변경)
    OAuth2UserInfo oAuth2UserInfo;

    switch (provider) {
      case NAVER:
        oAuth2UserInfo = NaverUserInfo.from(attributes, clientId, provider);
        break;

      // 다른 OAuth 추가 시 같은 포맷으로 작성

      default:
        throw new MemberException(OAUTH_PROVIDER_NOT_VALID_EXCEPTION);
    }

    // 사용자 정보로부터 회원 엔티티 조회
    Member member = memberRepository.findByEmail(oAuth2UserInfo.getEmail()).orElse(null);

    if (member == null) {
      member = memberRepository.save(
          Member.builder()
              .profileImageFileName(null)
              .name(oAuth2UserInfo.getName())
              .phone(oAuth2UserInfo.getPhone())
              .email(oAuth2UserInfo.getEmail())
              .password(passwordEncoder.encode(oAuth2UserInfo.getEmail()))
              .status(ACTIVE)
              .role(MEMBER)
              .provider(oAuth2UserInfo.getProvider())
              .build()
      );
    }

    // 최종적으로 사용자 정보와 회원 엔티티를 담아 OAuthMemberAdapter 객체를 반환
    return OAuthMemberAdapter.from(member, attributes);
  }
}
