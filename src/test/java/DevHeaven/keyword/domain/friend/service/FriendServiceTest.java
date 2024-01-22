package DevHeaven.keyword.domain.friend.service;

import static DevHeaven.keyword.support.fixture.FriendFixture.FRIEND_ACCEPT_1;
import static DevHeaven.keyword.support.fixture.FriendFixture.FRIEND_ACCEPT_2;
import static DevHeaven.keyword.support.fixture.MemberFixture.CAT;
import static DevHeaven.keyword.support.fixture.MemberFixture.DOG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import DevHeaven.keyword.common.exception.FriendException;
import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.domain.friend.entity.Friend;
import DevHeaven.keyword.domain.friend.repository.FriendRepository;
import DevHeaven.keyword.domain.friend.type.FriendStatus;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import DevHeaven.keyword.support.fixture.MemberFixture;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FriendServiceTest {

  @Mock
  private FriendRepository friendRepository;

  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private FriendService friendService;

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
    when(friendRepository.findByMemberRequestMemberIdAndFriendMemberIdAndStatus(memberRequest.getMemberId(), friend.getMemberId(), FriendStatus.FRIEND_ACCEPTED)).thenReturn(Optional.of(memberToFriend));
    when(friendRepository.findByMemberRequestMemberIdAndFriendMemberIdAndStatus(friend.getMemberId(), memberRequest.getMemberId(), FriendStatus.FRIEND_ACCEPTED)).thenReturn(Optional.of(friendToMember));

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