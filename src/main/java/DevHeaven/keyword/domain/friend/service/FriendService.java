package DevHeaven.keyword.domain.friend.service;

import static DevHeaven.keyword.common.exception.type.ErrorCode.*;
import static DevHeaven.keyword.domain.friend.type.FriendStatus.*;

import DevHeaven.keyword.common.aop.DistributedLock;
import DevHeaven.keyword.common.exception.FriendException;
import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.common.exception.type.ErrorCode;
import DevHeaven.keyword.domain.friend.entity.Friend;
import DevHeaven.keyword.domain.friend.repository.FriendRepository;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.core.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FriendService {

  private final FriendRepository friendRepository;
  private final MemberRepository memberRepository;
  @DistributedLock(key = "RequestFriend")
  public boolean requestFriend(final Long friendId){
    // TODO : 시큐리티 적용후 멤버관련 유효성 검사 추가 (임시 방편으로 해둠)
    final Member requestMember = Member.builder().id(1L).build();
    final Member friend = memberRepository.findById(friendId).orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

    //이미 친구인지, 친구 요청을 받는 쪽에서 이미 요청을 했는지 확인(친구 요청 유효성 검사)
    validateFriendRequest(requestMember, friend);

    //처음 친구 요청이면 insert하고 친구 삭제,거절후 또 요청한거면 update 실행
    final Optional <Friend> friendRequest = friendRepository.findFriendRequest(requestMember.getId() ,
        friend.getId() , List.of(FRIEND_REFUSED.toString() ,
            FRIEND_DELETE.toString()));

    if(friendRequest.isPresent()){
      final Friend friendPreRequest = friendRequest.get();
      friendPreRequest.modifyFriendStatus(FRIEND_CHECKING);
    }else{
      final Friend friendFirstRequest = Friend.builder()
          .memberRequest(requestMember)
          .friend(friend)
          .status(FRIEND_CHECKING)
          .build();
      friendRepository.save(friendFirstRequest);
    }

    return true;
  }

  private void validateFriendRequest(final Member requestMember ,final Member friend) {
    //요청 A, 요청받은 B
    //A -> B 가 이미 친구 인지
    final boolean friendExist = friendRepository.findByMemberRequestIdAndFriendIdAndStatus(
        requestMember.getId() , friend.getId() , FRIEND_ACCEPTED).isPresent();
    if(friendExist) {
      throw new FriendException(ErrorCode.FRIEND_ALREADY);
    }

    //B -> A 가 이미 요청한 기록이 있는지
    final boolean friendRequestExist = friendRepository.findByMemberRequestIdAndFriendIdAndStatus(friend.getId() ,
        requestMember.getId() , FRIEND_CHECKING).isPresent();
    if(friendRequestExist) {
      throw new FriendException(ErrorCode.FRIEND_REQUEST_ALREADY);
    }
  }

  @Transactional
  public boolean deleteFriend(final Long memberRequestId) {
    // TODO : 시큐리티 적용후 멤버관련 유효성 검사 추가 (임시 방편으로 해둠)
    final Member requestMember = Member.builder()
        .id(1L)
        .build();

    final Member friend = memberRepository.findById(memberRequestId)
        .orElseThrow(() -> new MemberException(
            MEMBER_NOT_FOUND));

    final Friend memberToFriend = friendRepository.findByMemberRequestIdAndFriendIdAndStatus(requestMember.getId(), friend.getId(), FRIEND_ACCEPTED)
        .orElseThrow(() -> new FriendException(FRIEND_NOT_FOUND));

    final Friend friendToMember = friendRepository.findByMemberRequestIdAndFriendIdAndStatus(friend.getId(), requestMember.getId(), FRIEND_ACCEPTED)
        .orElseThrow(() -> new FriendException(FRIEND_NOT_FOUND));

    memberToFriend.modifyFriendStatus(FRIEND_DELETE);
    friendToMember.modifyFriendStatus(FRIEND_DELETE);

    return true;
  }
}
