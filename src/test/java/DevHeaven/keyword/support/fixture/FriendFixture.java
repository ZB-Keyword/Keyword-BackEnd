package DevHeaven.keyword.support.fixture;

import DevHeaven.keyword.domain.friend.entity.Friend;
import DevHeaven.keyword.domain.friend.type.FriendStatus;
import DevHeaven.keyword.domain.member.entity.Member;

public enum FriendFixture {
  FRIEND_CHECKING(1L,FriendStatus.FRIEND_CHECKING),
  FRIEND_ACCEPT_1(2L,FriendStatus.FRIEND_ACCEPTED),
  FRIEND_ACCEPT_2(3L,FriendStatus.FRIEND_ACCEPTED),
  FRIEND_REFUSE(4L,FriendStatus.FRIEND_REFUSED),
  FRIEND_DELETE_1(5L,FriendStatus.FRIEND_DELETE),
  FRIEND_DELETE_2(6L,FriendStatus.FRIEND_DELETE);

  private Long id;
  private FriendStatus status;

  FriendFixture(Long id , FriendStatus status) {
    this.id = id;
    this.status = status;
  }

  public Friend createFriend(Member memberRequest, Member friend){
    return Friend.builder()
        .id(this.id)
        .status(this.status)
        .memberRequest(memberRequest)
        .friend(friend)
        .build();
  }
}
