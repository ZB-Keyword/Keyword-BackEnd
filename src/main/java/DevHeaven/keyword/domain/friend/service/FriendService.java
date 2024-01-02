package DevHeaven.keyword.domain.friend.service;

import static DevHeaven.keyword.common.exception.type.ErrorCode.*;

import DevHeaven.keyword.common.exception.FriendNotFoundException;
import DevHeaven.keyword.common.exception.MemberNotFoundException;
import DevHeaven.keyword.common.exception.type.ErrorCode;
import DevHeaven.keyword.domain.friend.entity.Friend;
import DevHeaven.keyword.domain.friend.repository.FriendRepository;
import DevHeaven.keyword.domain.friend.type.FriendStatus;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendService {

  private final FriendRepository friendRepository;
  private final MemberRepository memberRepository;

  public void deleteFriend(final Long memberRequestId) {
    // TODO : 시큐리티 적용후 멤버관련 유효성 검사 추가 (임시 방편으로 해둠)
    final Member requestMember = new Member();

    final Member friend = memberRepository.findById(memberRequestId)
        .orElseThrow(() -> new MemberNotFoundException(
            MEMBER_NOT_FOUND));

    final Friend memberAndFriend = friendRepository.findByMemberRequestAndFriendAndStatus(requestMember , friend, FriendStatus.FRIEND_ACCEPTED)
        .orElseThrow(() -> new FriendNotFoundException(FRIEND_NOT_FOUND));

    final Friend friendAndMember = friendRepository.findByMemberRequestAndFriendAndStatus(friend , requestMember, FriendStatus.FRIEND_ACCEPTED)
        .orElseThrow(() -> new FriendNotFoundException(FRIEND_NOT_FOUND));

    memberAndFriend.modifyFriendStatus(FriendStatus.FRIEND_DELETE);
    friendAndMember.modifyFriendStatus(FriendStatus.FRIEND_DELETE);
  }
}
