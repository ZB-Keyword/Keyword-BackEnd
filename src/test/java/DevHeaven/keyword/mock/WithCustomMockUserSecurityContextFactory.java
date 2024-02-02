package DevHeaven.keyword.mock;

import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.support.fixture.MemberFixture;
import java.util.Arrays;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import static DevHeaven.keyword.domain.member.type.MemberRole.MEMBER;
import static DevHeaven.keyword.support.fixture.MemberFixture.DOG;

public class WithCustomMockUserSecurityContextFactory implements
    WithSecurityContextFactory<WithCustomMockUser> {

  @Override
  public SecurityContext createSecurityContext(WithCustomMockUser annotation) {

    MemberFixture securityTestMemberFixture = DOG;

    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(
        new UsernamePasswordAuthenticationToken(
            MemberAdapter.from(securityTestMemberFixture.createMember()),
            securityTestMemberFixture.getPassword(),
            Arrays.asList(new SimpleGrantedAuthority(securityTestMemberFixture.getRole().name()))));
    SecurityContextHolder.setContext(securityContext);

    return securityContext;
  }
}
