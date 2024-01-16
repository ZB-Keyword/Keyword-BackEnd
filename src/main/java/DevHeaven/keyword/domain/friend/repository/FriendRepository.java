package DevHeaven.keyword.domain.friend.repository;

import DevHeaven.keyword.domain.friend.entity.Friend;
import DevHeaven.keyword.domain.friend.type.FriendStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendRepository extends JpaRepository<Friend, Long> {

  Optional<Friend> findByMemberRequestMemberIdAndFriendMemberIdAndStatus(Long memberRequestId,
      Long friend, FriendStatus friendStatus);

  @Query("SELECT friend FROM Friend friend  WHERE friend.memberRequest.memberId = :memberRequestId "
      + "AND friend.friend.memberId = :friendId AND friend.status IN (:statusList)")
  Optional<Friend> findFriendRequest(@Param("memberRequestId") Long memberRequestId,
      @Param("friendId") Long friendId, @Param("statusList") List <FriendStatus> statusList);
}
