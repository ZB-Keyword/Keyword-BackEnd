package DevHeaven.keyword.domain.member.dto.provider;

import DevHeaven.keyword.domain.member.type.MemberProvider;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NaverUserInfo implements OAuth2UserInfo {

    private static final String SCOPE_NAME = "name";
    private static final String SCOPE_EMAIL = "email";
    private static final String SCOPE_PHONE = "mobile";

    private Map<String, Object> attributes; // OAuth2회원이 들고 있는 getAttributes()
    private String providerId;
    private MemberProvider provider;
    private String email;
    private String name;
    private String phone;

    public static NaverUserInfo from(Map<String, Object> attributes, String providerId, MemberProvider provider) {
        return NaverUserInfo.builder()
            .attributes(attributes)
            .providerId(providerId)
            .provider(provider)
            .email((String) attributes.get(SCOPE_EMAIL))
            .name((String) attributes.get(SCOPE_NAME))
            .phone((String) attributes.get(SCOPE_PHONE))
            .build();
    }
}
