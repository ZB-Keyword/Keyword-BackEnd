package DevHeaven.keyword.domain.friend.repository;

import DevHeaven.keyword.domain.friend.entity.Friend;
import DevHeaven.keyword.domain.friend.type.FriendStatus;
import DevHeaven.keyword.domain.member.entity.Member;
import java.util.List;
import java.util.Optional;
import org.elasticsearch.monitor.os.OsStats.Mem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendRepository extends JpaRepository<Friend, Long>  {

  Optional<Friend> findByMemberRequestMemberIdAndFriendMemberIdAndStatus(Long memberRequestId,
      Long friend, FriendStatus friendStatus);

  @Query("SELECT friend FROM Friend friend  WHERE friend.memberRequest.memberId = :memberRequestId "
      + "AND friend.friend.memberId = :friendId AND friend.status IN (:statusList)")
  Optional<Friend> findFriendRequest(@Param("memberRequestId") Long memberRequestId,
      @Param("friendId") Long friendId, @Param("statusList") List <FriendStatus> statusList);

  @Query("SELECT f.friend.memberId FROM Friend f JOIN f.friend friend JOIN f.memberRequest memberRequest "
      + "WHERE memberRequest.memberId = :memberRequestId AND f.status = :friendStatus ORDER BY f.friend.name")
  Page<Long> findFriendListByMemberId(@Param("memberRequestId") Long memberRequestId,
      @Param("friendStatus") FriendStatus friendStatus,
      Pageable pageable);
  @Query("SELECT f.memberRequest.memberId FROM Friend f JOIN f.friend friend JOIN f.memberRequest memberRequest "
      + "WHERE friend.memberId = :friendMemberId AND f.status = :friendStatus ORDER BY f.memberRequest.name")
  Page<Long> findFriendListByFriendId(@Param("friendMemberId") Long friendMemberId,
      @Param("friendStatus") FriendStatus friendStatus,
      Pageable pageable);


  Optional<Friend> findByIdAndStatus(Long friendId, FriendStatus friendStatus);

}
