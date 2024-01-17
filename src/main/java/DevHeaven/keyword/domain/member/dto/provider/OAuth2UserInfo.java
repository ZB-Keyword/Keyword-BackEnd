package DevHeaven.keyword.domain.member.dto.provider;

import DevHeaven.keyword.domain.member.type.MemberProvider;

public interface OAuth2UserInfo {

    String getProviderId();
    MemberProvider getProvider();
    String getEmail();
    String getName();
    String getPhone();
}
