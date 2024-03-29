package DevHeaven.keyword.domain.friend.service;

import DevHeaven.keyword.common.aop.DistributedLock;
import DevHeaven.keyword.common.exception.FriendException;
import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.common.exception.NoticeException;
import DevHeaven.keyword.common.exception.type.ErrorCode;
import DevHeaven.keyword.common.service.image.AmazonS3FileService;
import DevHeaven.keyword.domain.friend.dto.request.FriendApproveRequest;
import DevHeaven.keyword.domain.friend.dto.request.FriendListStatusRequest;
import DevHeaven.keyword.domain.friend.dto.request.FriendSearchListRequest;
import DevHeaven.keyword.domain.friend.dto.response.FriendListResponse;
import DevHeaven.keyword.domain.friend.dto.response.FriendSearchListResponse;
import DevHeaven.keyword.domain.friend.entity.Friend;
import DevHeaven.keyword.domain.friend.repository.FriendRepository;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import DevHeaven.keyword.domain.notice.dto.NoticeEvent;
import DevHeaven.keyword.domain.notice.entity.Notice;
import DevHeaven.keyword.domain.notice.repository.NoticeRepository;
import DevHeaven.keyword.domain.notice.type.NoticeType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static DevHeaven.keyword.common.exception.type.ErrorCode.*;
import static DevHeaven.keyword.domain.friend.dto.request.FriendListStatusRequest.REQUEST;
import static DevHeaven.keyword.domain.friend.dto.request.FriendListStatusRequest.REQUESTED;
import static DevHeaven.keyword.domain.friend.type.FriendStatus.*;

@Service
@RequiredArgsConstructor
public class FriendService {

  private final FriendRepository friendRepository;
  private final MemberRepository memberRepository;
  private final NoticeRepository noticeRepository;
  private final AmazonS3FileService fileService;
  private final ApplicationEventPublisher applicationEventPublisher;

  public List <FriendListResponse> getFriendList(final MemberAdapter memberAdapter ,final FriendListStatusRequest friendState,
      final Long noticeId, final Pageable pageable){
    final Member requestMember = memberRepository.findByEmail(memberAdapter.getEmail())
        .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

    if(noticeId != null) {
      final Notice notice = noticeRepository.findById(noticeId)
          .orElseThrow(() -> new NoticeException(ErrorCode.NOTICE_NOT_FOUND));
      notice.modifyNoticeIsRead();
    }

    List <Long> friendIds = null;
    if(friendState == REQUEST) {
      //내가 친구 요청한 목록 Checking
      friendIds = friendRepository.findFriendListByMemberId(
          requestMember.getMemberId() , FRIEND_CHECKING , pageable).getContent();

    }else if(friendState == REQUESTED) {
      //내가 친구 요청을 받은 목록
      friendIds = friendRepository.findFriendListByFriendId(
          requestMember.getMemberId() , FRIEND_CHECKING , pageable).getContent();

    }else {
      friendIds  = friendRepository.findFriendListByMemberId(
          requestMember.getMemberId() , FRIEND_ACCEPTED , pageable).getContent();

    }

    final List <Member> friendInfos = memberRepository.findByMemberIdIn(friendIds);
    return from(friendInfos);
  }

  private List<FriendListResponse> from(List <Member> friendInfos){
    return friendInfos.stream().map(
        friendInfo -> {
          FriendListResponse friendListResponse = FriendListResponse.from(friendInfo);
          friendListResponse.modifyImageUrl(fileService.createUrl(friendInfo.getProfileImageFileName()));
          return friendListResponse;
        })
        .collect(Collectors.toList());
  }
  
  @DistributedLock(key = "RequestFriend")
  public boolean requestFriend(final MemberAdapter memberAdapter, final Long friendId) {
    final Member requestMember = memberRepository.findByEmail(memberAdapter.getEmail())
        .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
    final Member friend = memberRepository.findById(friendId)
        .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

    //이미 친구인지, 친구 요청을 받는 쪽에서 이미 요청을 했는지 확인(친구 요청 유효성 검사)
    validateFriendRequest(requestMember, friend);

    //처음 친구 요청이면 insert하고 친구 삭제,거절후 또 요청한거면 update 실행
    final Optional<Friend> friendRequest = friendRepository.findFriendRequest(
        requestMember.getMemberId(),
        friend.getMemberId(), Arrays.asList(FRIEND_REFUSED, FRIEND_DELETE));

    if (friendRequest.isPresent()) {
      final Friend friendPreRequest = friendRequest.get();
      friendPreRequest.modifyFriendStatus(FRIEND_CHECKING);
      sendNotice(friendPreRequest, friend.getMemberId());
    } else {
      final Friend friendFirstRequest = Friend.builder()
          .memberRequest(requestMember)
          .friend(friend)
          .status(FRIEND_CHECKING)
          .build();
      friendRepository.save(friendFirstRequest);
      sendNotice(friendFirstRequest, friend.getMemberId());
    }

    return true;
  }



  private void sendNotice(final Friend friendRequest, final Long memberId) {
    applicationEventPublisher.publishEvent(
        NoticeEvent.builder()
            .id(friendRequest.getId())
            .memberId(memberId)
            .noticeType(NoticeType.FRIEND_REQUEST)
            .build()
    );
  }

  private void validateFriendRequest(final Member requestMember, final Member friend) {
    //요청 A, 요청받은 B
    //A -> B 가 이미 친구 인지
    final boolean friendExist = friendRepository.findByMemberRequestMemberIdAndFriendMemberIdAndStatus(
        requestMember.getMemberId(), friend.getMemberId(), FRIEND_ACCEPTED).isPresent();
    if (friendExist) {
      throw new FriendException(ErrorCode.FRIEND_ALREADY);
    }

    //B -> A 가 이미 요청한 기록이 있는지
    final boolean friendRequestExist = friendRepository.findByMemberRequestMemberIdAndFriendMemberIdAndStatus(
        friend.getMemberId(),
        requestMember.getMemberId(), FRIEND_CHECKING).isPresent();
    if (friendRequestExist) {
      throw new FriendException(ErrorCode.FRIEND_REQUEST_ALREADY);
    }
  }

  @Transactional
  public boolean deleteFriend(final MemberAdapter memberAdapter, final Long memberRequestId) {
    // TODO : 시큐리티 적용후 멤버관련 유효성 검사 추가 (임시 방편으로 해둠)
    final Member requestMember = memberRepository.findByEmail(memberAdapter.getEmail())
        .orElseThrow(() -> new MemberException(
            MEMBER_NOT_FOUND));

    final Member friend = memberRepository.findById(memberRequestId)
        .orElseThrow(() -> new MemberException(
            MEMBER_NOT_FOUND));

    final Friend memberToFriend = friendRepository.findByMemberRequestMemberIdAndFriendMemberIdAndStatus(
            requestMember.getMemberId(), friend.getMemberId(), FRIEND_ACCEPTED)
        .orElseThrow(() -> new FriendException(FRIEND_NOT_FOUND));

    final Friend friendToMember = friendRepository.findByMemberRequestMemberIdAndFriendMemberIdAndStatus(
            friend.getMemberId(), requestMember.getMemberId(), FRIEND_ACCEPTED)
        .orElseThrow(() -> new FriendException(FRIEND_NOT_FOUND));

    memberToFriend.modifyFriendStatus(FRIEND_DELETE);
    friendToMember.modifyFriendStatus(FRIEND_DELETE);

    return true;
  }

  @Transactional
  public boolean handleFriendRequest(final MemberAdapter memberAdapter, final Long memberReqId, final FriendApproveRequest friendApproveRequest) {

    final Member acceptingMember = memberRepository.findByEmail(memberAdapter.getEmail())
            .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

    final Friend friendRequest = friendRepository.findByMemberRequestMemberIdAndFriendMemberIdAndStatus(memberReqId, acceptingMember.getMemberId(),FRIEND_CHECKING)
            .orElseThrow(() -> new FriendException(FRIEND_NOT_FOUND));

    if (friendRequest.getFriend().getMemberId() != acceptingMember.getMemberId()) {
      throw new FriendException(FRIEND_REQUEST_INVALID);
    }

    friendRequest.modifyFriendStatus(friendApproveRequest.getFriendStatus());

    //처음 친구 맺는 경우가 아니라면
    final Optional <Friend> friendToMember = friendRepository.findFriendRequest(
        acceptingMember.getMemberId() , friendRequest.getMemberRequest().getMemberId() ,
        Arrays.asList(FRIEND_REFUSED , FRIEND_DELETE));

    if(friendToMember.isPresent()){
      friendToMember.get().modifyFriendStatus(friendApproveRequest.getFriendStatus());
    }else if(friendApproveRequest.getFriendStatus() == FRIEND_ACCEPTED){

      final Friend friendFirstRequest = Friend.builder()
          .memberRequest(acceptingMember)
          .friend(friendRequest.getMemberRequest())
          .status(friendApproveRequest.getFriendStatus())
          .build();
      friendRepository.save(friendFirstRequest);

    }

    return true;
  }

}
