package DevHeaven.keyword.domain.friend.repository;

import DevHeaven.keyword.domain.friend.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {
}
