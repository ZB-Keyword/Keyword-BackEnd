package DevHeaven.keyword.domain.member.repository;

import DevHeaven.keyword.domain.member.entity.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    List <Member> findByMemberIdIn(List<Long> memberId);
}
