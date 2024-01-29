package DevHeaven.keyword.mock;

import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.support.fixture.MemberFixture;
import java.util.Arrays;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithCustomMockUserSecurityContextFactory implements
    WithSecurityContextFactory <WithCustomMockUser> {

  @Override
  public SecurityContext createSecurityContext(WithCustomMockUser annotation) {

    MemberAdapter memberAdapter = MemberAdapter.from(MemberFixture.DOG.createMember());
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(memberAdapter,
        "password", Arrays.asList(new SimpleGrantedAuthority("MEMBER"))));
    SecurityContextHolder.setContext(securityContext);

    return securityContext;
  }
}
