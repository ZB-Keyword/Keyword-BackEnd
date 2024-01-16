package DevHeaven.keyword.domain.member.service.oauth.provider;

public interface OAuth2UserInfo {

    String getProviderId();
    String getProvider();
    String getEmail();
    String getName();
    String getPhone();


}
