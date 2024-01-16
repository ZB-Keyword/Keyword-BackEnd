package DevHeaven.keyword.domain.friend.repository;

import DevHeaven.keyword.domain.friend.entity.Friend;
import DevHeaven.keyword.domain.friend.type.FriendStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {

  Optional<Friend> findByMemberRequestMemberIdAndFriendMemberIdAndStatus(Long memberRequestId, Long friend, FriendStatus friendStatus);
}
