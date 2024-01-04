package DevHeaven.keyword.domain.friend.service;

import static DevHeaven.keyword.common.exception.type.ErrorCode.*;
import static DevHeaven.keyword.domain.friend.type.FriendStatus.*;

import DevHeaven.keyword.common.exception.FriendException;
import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.domain.friend.dto.response.FriendDeleteResponse;
import DevHeaven.keyword.domain.friend.entity.Friend;
import DevHeaven.keyword.domain.friend.repository.FriendRepository;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FriendService {

  private final FriendRepository friendRepository;
  private final MemberRepository memberRepository;
  @Transactional
  public FriendDeleteResponse deleteFriend(final Long memberRequestId) {
    // TODO : 시큐리티 적용후 멤버관련 유효성 검사 추가 (임시 방편으로 해둠)
    final Member requestMember = new Member();

    final Member friend = memberRepository.findById(memberRequestId)
        .orElseThrow(() -> new MemberException(
            MEMBER_NOT_FOUND));

    final Friend memberToFriend = friendRepository.findByMemberRequestAndFriendAndStatus(requestMember, friend, FRIEND_ACCEPTED)
        .orElseThrow(() -> new FriendException(FRIEND_NOT_FOUND));

    final Friend friendToMember = friendRepository.findByMemberRequestAndFriendAndStatus(friend, requestMember, FRIEND_ACCEPTED)
        .orElseThrow(() -> new FriendException(FRIEND_NOT_FOUND));

    memberToFriend.modifyFriendStatus(FRIEND_DELETE);
    friendToMember.modifyFriendStatus(FRIEND_DELETE);

    return FriendDeleteResponse.builder()
        .isFriendDelete(true)
        .build();
  }
}
