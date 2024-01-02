package DevHeaven.keyword.domain.member.repository;

import DevHeaven.keyword.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
