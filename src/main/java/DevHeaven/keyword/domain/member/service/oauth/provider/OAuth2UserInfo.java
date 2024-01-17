package DevHeaven.keyword.domain.member.service.oauth.provider;

import DevHeaven.keyword.domain.member.type.MemberProviderType;

public interface OAuth2UserInfo {

    String getProviderId();
    MemberProviderType getProvider();
    String getEmail();
    String getName();
    String getPhone();
}
