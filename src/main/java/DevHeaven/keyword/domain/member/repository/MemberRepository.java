package DevHeaven.keyword.domain.member.repository;

import DevHeaven.keyword.domain.friend.entity.Friend;
import DevHeaven.keyword.domain.friend.type.FriendStatus;
import DevHeaven.keyword.domain.member.entity.Member;
import java.util.List;

import DevHeaven.keyword.domain.member.type.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    List<Member> findAllByStatus(MemberStatus memberStatus);
    List <Member> findByMemberIdIn(List<Long> memberId);

    // 임시 메서드

    @Query(nativeQuery = true,
    value = "SELECT * FROM member WHERE name like '%(:keyword)%' or email like '%(:keyword)%' ",
    countQuery = "SELECT COUNT(*) FROM member WHERE name like '%(:keyword)%' or email like '%(:keyword)%'; ")
    Page<Member> findAllByNameOrEmailContainingKeyword(String keyword, Pageable pageable);

    Page<Member> findAllByNameContainingOrEmailContaining(String keyword, String keyword2, Pageable pageable);
}
