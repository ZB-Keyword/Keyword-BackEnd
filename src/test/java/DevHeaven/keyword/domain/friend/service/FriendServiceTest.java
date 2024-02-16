package DevHeaven.keyword.domain.friend.service;

import static DevHeaven.keyword.domain.friend.dto.request.FriendListStatusRequest.REQUEST;
import static DevHeaven.keyword.domain.friend.type.FriendStatus.FRIEND_ACCEPTED;
import static DevHeaven.keyword.support.fixture.FriendFixture.FRIEND_ACCEPT_1;
import static DevHeaven.keyword.support.fixture.FriendFixture.FRIEND_ACCEPT_2;
import static DevHeaven.keyword.support.fixture.MemberFixture.CAT;
import static DevHeaven.keyword.support.fixture.MemberFixture.DOG;
import static DevHeaven.keyword.support.fixture.MemberFixture.LION;
import static DevHeaven.keyword.support.fixture.MemberFixture.PANDA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import DevHeaven.keyword.common.exception.FriendException;
import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.common.exception.NoticeException;
import DevHeaven.keyword.common.service.image.AmazonS3FileService;
import DevHeaven.keyword.domain.friend.dto.request.FriendListStatusRequest;
import DevHeaven.keyword.domain.friend.dto.response.FriendListResponse;
import DevHeaven.keyword.domain.friend.entity.Friend;
import DevHeaven.keyword.domain.friend.repository.FriendRepository;
import DevHeaven.keyword.domain.friend.type.FriendStatus;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import DevHeaven.keyword.domain.notice.repository.NoticeRepository;
import DevHeaven.keyword.support.fixture.FriendFixture;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class FriendServiceTest {

  @Mock
  private FriendRepository friendRepository;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private NoticeRepository noticeRepository;

  @Mock
  private AmazonS3FileService fileService;

  @Mock
  private ApplicationEventPublisher applicationEventPublisher;

  @InjectMocks
  private FriendService friendService;
  @Test
  @DisplayName("내 친구 목록 가져오기 - 성공")
  void getFriendList_success(){
    //given
    Member memberRequest = DOG.createMember();
    FriendListStatusRequest statusRequest = REQUEST;
    Pageable pageable = PageRequest.of(0,10);

    List<Member> friendInfos=Arrays.asList(CAT.createMember(),PANDA.createMember(),LION.createMember());

    Page<Long> friendIds = new PageImpl <>(Arrays.asList(2L, 3L, 4L), PageRequest.of(0, 10), 3);

    when(memberRepository.findByEmail(memberRequest.getEmail())).thenReturn(Optional.of(memberRequest));
    when(friendRepository.findFriendListByMemberId(memberRequest.getMemberId(), FriendStatus.FRIEND_CHECKING, pageable)).thenReturn(friendIds);
    when(memberRepository.findByMemberIdIn(friendIds.toList())).thenReturn(friendInfos);

    //when
    List <FriendListResponse> friendList = friendService.getFriendList(
        MemberAdapter.from(DOG.createMember()) , statusRequest , null , pageable);

    //then
    Assertions.assertEquals(friendInfos.size(),friendList.size());
  }

  @Test
  @DisplayName("내 친구 목록 가져오기 - 실패 : 회원을 찾을 수 없음")
  void getFriendList_fail_not_found_member(){
    //given
    Member memberRequest = DOG.createMember();
    FriendListStatusRequest statusRequest = REQUEST;
    Long noticeId=1L;
    Pageable pageable = PageRequest.of(0,10);

    when(memberRepository.findByEmail(memberRequest.getEmail())).thenReturn(Optional.empty());

    //when
    Assertions.assertThrows(MemberException.class,() -> friendService.getFriendList
        (MemberAdapter.from(DOG.createMember()),statusRequest,noticeId,pageable));
  }

  @Test
  @DisplayName("내 친구 목록 가져오기 - 실패 : 알림을 찾을 수 없음")
  void getFriendList_fail_not_found_notice(){
    //given
    Member memberRequest = DOG.createMember();
    FriendListStatusRequest statusRequest = REQUEST;
    Long noticeId=1L;
    Pageable pageable = PageRequest.of(0,10);

    List<Member> friendInfos=Arrays.asList(CAT.createMember(),PANDA.createMember(),LION.createMember());

    Page<Long> friendIds = new PageImpl <>(Arrays.asList(2L, 3L, 4L), PageRequest.of(0, 10), 3);

    when(memberRepository.findByEmail(memberRequest.getEmail())).thenReturn(Optional.of(memberRequest));
    when(noticeRepository.findById(noticeId)).thenReturn(Optional.empty());

    //when
    Assertions.assertThrows(NoticeException.class,() -> friendService.getFriendList
        (MemberAdapter.from(DOG.createMember()),statusRequest,noticeId,pageable));
  }

  @Test
  @DisplayName("친구 요청 - 성공")
  void requestFriend_success(){
    //given
    Member memberRequest = DOG.createMember();
    MemberAdapter memberAdapter = MemberAdapter.from(memberRequest);
    Member friend = CAT.createMember();
    Long friendId = friend.getMemberId();

    when(memberRepository.findByEmail(memberAdapter.getEmail())).thenReturn(Optional.of(memberRequest));
    when(memberRepository.findById(friendId)).thenReturn(Optional.of(friend));
    when(friendRepository.findByMemberRequestMemberIdAndFriendMemberIdAndStatus(memberRequest.getMemberId(),friendId,
        FRIEND_ACCEPTED)).thenReturn(Optional.empty());
    when(friendRepository.findByMemberRequestMemberIdAndFriendMemberIdAndStatus(friendId, memberRequest.getMemberId(),
        FriendStatus.FRIEND_CHECKING)).thenReturn(Optional.empty());

    //when
    boolean requestSuccess = friendService.requestFriend(memberAdapter , friendId);

    //then
    Assertions.assertEquals(requestSuccess, true);
  }

  @Test
  @DisplayName("친구 요청 - 실패 : 회원을 찾을 수 없음")
  void requestFriend_fail_not_found_member(){
    //given
    Member memberRequest = DOG.createMember();
    MemberAdapter memberAdapter = MemberAdapter.from(memberRequest);
    Member friend = CAT.createMember();
    Long friendId = friend.getMemberId();

    when(memberRepository.findByEmail(memberAdapter.getEmail())).thenReturn(Optional.empty());

    //when
    //then
    Assertions.assertThrows(MemberException.class, ()-> friendService.requestFriend(memberAdapter , friendId));
  }

  @Test
  @DisplayName("친구 요청 - 실패 : 친구 회원을 찾을 수 없음")
  void requestFriend_fail_not_found_friend_member(){
    //given
    Member memberRequest = DOG.createMember();
    MemberAdapter memberAdapter = MemberAdapter.from(memberRequest);
    Member friend = CAT.createMember();
    Long friendId = friend.getMemberId();

    when(memberRepository.findByEmail(memberAdapter.getEmail())).thenReturn(Optional.of(memberRequest));
    when(memberRepository.findById(friendId)).thenReturn(Optional.empty());

    //when
    //then
    Assertions.assertThrows(MemberException.class, ()-> friendService.requestFriend(memberAdapter , friendId));
  }

  @Test
  @DisplayName("친구 요청 - 실패 : 이미 친구임")
  void requestFriend_fail_already_friend(){
    //given
    Member memberRequest = DOG.createMember();
    MemberAdapter memberAdapter = MemberAdapter.from(memberRequest);
    Member friend = CAT.createMember();
    Long friendId = friend.getMemberId();

    Friend friend1 = FRIEND_ACCEPT_1.createFriend(memberRequest, friend);

    when(memberRepository.findByEmail(memberAdapter.getEmail())).thenReturn(Optional.of(memberRequest));
    when(memberRepository.findById(friendId)).thenReturn(Optional.of(friend));
    when(friendRepository.findByMemberRequestMemberIdAndFriendMemberIdAndStatus(memberRequest.getMemberId(),friendId,
        FRIEND_ACCEPTED)).thenReturn(Optional.of(friend1));


    //when
    //then
    Assertions.assertThrows(FriendException.class, ()-> friendService.requestFriend(memberAdapter , friendId));
  }

  @Test
  @DisplayName("친구 요청 - 실패 : 친구가 나에게 친구요청을 함")
  void requestFriend_fail_friend_request(){
    //given
    Member memberRequest = DOG.createMember();
    MemberAdapter memberAdapter = MemberAdapter.from(memberRequest);
    Member friend = CAT.createMember();
    Long friendId = friend.getMemberId();

    Friend friend2 = FriendFixture.FRIEND_CHECKING.createFriend(friend, memberRequest);

    when(memberRepository.findByEmail(memberAdapter.getEmail())).thenReturn(Optional.of(memberRequest));
    when(memberRepository.findById(friendId)).thenReturn(Optional.of(friend));
    when(friendRepository.findByMemberRequestMemberIdAndFriendMemberIdAndStatus(memberRequest.getMemberId(),friendId,
        FRIEND_ACCEPTED)).thenReturn(Optional.empty());
    when(friendRepository.findByMemberRequestMemberIdAndFriendMemberIdAndStatus(friendId, memberRequest.getMemberId(),
        FriendStatus.FRIEND_CHECKING)).thenReturn(Optional.of(friend2));

    //when
    //then
    Assertions.assertThrows(FriendException.class, ()-> friendService.requestFriend(memberAdapter , friendId));
  }

  @Test
  @DisplayName("친구 삭제 - 성공")
  void deleteFriend_success(){
    //given
    Member memberRequest = DOG.createMember();
    MemberAdapter memberAdapter = MemberAdapter.from(memberRequest);
    Member friend = CAT.createMember();

    Friend memberToFriend= FRIEND_ACCEPT_1.createFriend(memberRequest,friend);
    Friend friendToMember= FRIEND_ACCEPT_2.createFriend(friend,memberRequest);

    when(memberRepository.findByEmail(memberAdapter.getEmail())).thenReturn(Optional.of(memberRequest));
    when(memberRepository.findById(friend.getMemberId())).thenReturn(Optional.of(friend));
    when(friendRepository.findByMemberRequestMemberIdAndFriendMemberIdAndStatus(memberRequest.getMemberId(), friend.getMemberId(), FRIEND_ACCEPTED)).thenReturn(Optional.of(memberToFriend));
    when(friendRepository.findByMemberRequestMemberIdAndFriendMemberIdAndStatus(friend.getMemberId(), memberRequest.getMemberId(), FRIEND_ACCEPTED)).thenReturn(Optional.of(friendToMember));

    //when
    boolean deleteResponse = friendService.deleteFriend(memberAdapter,friend.getMemberId());

    //then
    assertThat(deleteResponse).isTrue();
    verify(memberRepository, times(1)).findById(anyLong());
    verify(friendRepository, times(2)).findByMemberRequestMemberIdAndFriendMemberIdAndStatus(any(),any(),any());
  }

  @Test
  @DisplayName("친구 삭제 - 실패 : 친구를 회원 DB에서 찾을 수 없음")
  void deleteFriend_fail_not_found_member() {
    //given
    Long memberRequestId=1L;
    Member memberRequest = DOG.createMember();
    MemberAdapter memberAdapter = MemberAdapter.from(memberRequest);
    given(memberRepository.findById(memberRequestId)).willReturn(Optional.empty());

    //when
    when(memberRepository.findByEmail(memberAdapter.getEmail())).thenReturn(Optional.of(memberRequest));

    //then
    Assertions.assertThrows(MemberException.class,()-> friendService.deleteFriend(memberAdapter,memberRequestId));
    verify(memberRepository, times(1)).findById(anyLong());
    verify(friendRepository, times(0)).findByMemberRequestMemberIdAndFriendMemberIdAndStatus(any(),any(),any());
  }

  @Test
  @DisplayName("친구 삭제 - 실패 : 친구 목록에서 ACCEPT 인 상태를 찾을 수 없음")
  void deleteFriend_fail_not_found_accepted_memberRequest() {
    //given
    Long memberRequestId=1L;
    Member memberRequest = DOG.createMember();
    MemberAdapter memberAdapter = MemberAdapter.from(memberRequest);
    Member friend = CAT.createMember();

    given(memberRepository.findById(memberRequestId)).willReturn(Optional.of(friend));
    given(friendRepository.findByMemberRequestMemberIdAndFriendMemberIdAndStatus(any(),any(),any())).willReturn(Optional.empty());

    //when
    when(memberRepository.findByEmail(memberAdapter.getEmail())).thenReturn(Optional.of(memberRequest));

    //then
    Assertions.assertThrows(FriendException.class,()-> friendService.deleteFriend(memberAdapter,memberRequestId));
    verify(memberRepository, times(1)).findById(anyLong());
    verify(friendRepository, times(1)).findByMemberRequestMemberIdAndFriendMemberIdAndStatus(any(),any(),any());
  }

}