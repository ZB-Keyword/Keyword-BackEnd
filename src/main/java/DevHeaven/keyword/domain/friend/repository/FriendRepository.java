package DevHeaven.keyword.domain.friend.repository;

import DevHeaven.keyword.domain.friend.entity.Friend;
import DevHeaven.keyword.domain.friend.type.FriendStatus;
import DevHeaven.keyword.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {

  Optional<Friend> findByMemberRequestAndFriendAndStatus(Member memberRequest, Member friend, FriendStatus friendStatus);
}
