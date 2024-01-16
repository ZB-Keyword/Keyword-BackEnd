package DevHeaven.keyword.domain.member.service.oauth;

import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.common.security.JwtUtils;
import DevHeaven.keyword.common.security.dto.TokenResponse;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import DevHeaven.keyword.domain.member.dto.PrincipalDetails;
import DevHeaven.keyword.domain.member.service.oauth.provider.NaverUserInfo;
import DevHeaven.keyword.domain.member.service.oauth.provider.OAuth2UserInfo;
import DevHeaven.keyword.domain.member.type.MemberRole;
import DevHeaven.keyword.domain.member.type.MemberStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static DevHeaven.keyword.common.exception.type.ErrorCode.MEMBER_NOT_FOUND;
import static DevHeaven.keyword.domain.member.type.MemberRole.MEMBER;
import static DevHeaven.keyword.domain.member.type.MemberStatus.ACTIVE;

@Slf4j
@Service
@RequiredArgsConstructor
public class Oauth2UserService extends DefaultOAuth2UserService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  // 네이버로 부터 받은 userRequest 데이터에 대한 후 처리 함수
  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

    // 네이버 로그인 창 -> 로그인 완료 -> code 리턴(Oauth-client 라이브러리) -> AccessToken 요청
    // userRequest 정보 -> loadUser 함수 호출 -> Oauth2(네이버)로 부터 정보 받아옴.
    OAuth2User oAuth2User = super.loadUser(userRequest);

    //OAuth2User의 attribute
    Map<String, Object> attributes = oAuth2User.getAttributes();

    // 출력: userRequest의 클라이언트 등록 정보, AccessToken, 사용자 정보
    System.out.println("userRequest: " + userRequest.getClientRegistration());
    System.out.println("getAccessToken: " + userRequest.getAccessToken().getTokenValue());
    System.out.println("getAttributes: " + super.loadUser(userRequest).getAttributes());

    // 사용자 정보를 제공하는 서비스 제공자 (Google, Naver 등)를 식별하기 위한 값
    String provider = userRequest.getClientRegistration().getClientId();
    // 서비스 제공자로부터 받은 사용자 식별 ID
    String providerId = userRequest.getClientRegistration().getClientId();

    OAuth2UserInfo oAuth2UserInfo;

    // Oauth2 사용자 정보 처리(Google, KaKao API 추가 시 변경)
    if (Objects.equals(userRequest.getClientRegistration().getRegistrationId(), "naver")) {
      oAuth2UserInfo = new NaverUserInfo(
          (Map<String, Object>) oAuth2User.getAttributes().get("response"));
    } else {
      throw new MemberException(MEMBER_NOT_FOUND);
    }

    // 사용자 정보로부터 회원 엔티티 조회
    Optional<Member> memberEntityOptional = memberRepository.findByEmail(
        oAuth2UserInfo.getProvider() + "_" + oAuth2UserInfo.getProviderId());

    // 만약 회원 엔티티가 존재하지 않으면 새로운 회원 엔티티를 생성하고 저장
    Member member;
    if (memberEntityOptional.isPresent()) {
      member = memberEntityOptional.get();
    } else {
      member = Member.builder()
          .name(oAuth2UserInfo.getName())
          .password(passwordEncoder.encode("설정비밀번호")) // 실제로는 안전한 방식으로 비밀번호를 저장해야 함
          .email(oAuth2UserInfo.getEmail())
          .provider(oAuth2UserInfo.getProvider())
          .providerId(oAuth2UserInfo.getProviderId())
          .phone(oAuth2UserInfo.getPhone())
          .status(ACTIVE)
          .role(MEMBER)
          .build();
      memberRepository.save(member);
    }

    // 최종적으로 사용자 정보와 회원 엔티티를 담아 PrincipalDetails 객체를 반환
    PrincipalDetails principalDetails = new PrincipalDetails(member, oAuth2User.getAttributes());

    return principalDetails;
  }
}
