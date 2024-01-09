package DevHeaven.keyword.domain.friend.service;

import static DevHeaven.keyword.support.fixture.FriendFixture.FRIEND_ACCEPT_1;
import static DevHeaven.keyword.support.fixture.FriendFixture.FRIEND_ACCEPT_2;
import static DevHeaven.keyword.support.fixture.MemberFixture.*;
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
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import DevHeaven.keyword.support.fixture.FriendFixture;
import DevHeaven.keyword.support.fixture.MemberFixture;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.elasticsearch.monitor.os.OsStats.Mem;
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
    Member friend = CAT.createMember();

    Friend memberToFriend= FRIEND_ACCEPT_1.createFriend(memberRequest,friend);
    Friend friendToMember= FRIEND_ACCEPT_2.createFriend(friend,memberRequest);

    when(memberRepository.findById(1L)).thenReturn(Optional.of(friend));
    when(friendRepository.findByMemberRequestIdAndFriendIdAndStatus(memberRequest.getId(), friend.getId(), FriendStatus.FRIEND_ACCEPTED)).thenReturn(Optional.of(memberToFriend));
    when(friendRepository.findByMemberRequestIdAndFriendIdAndStatus(friend.getId(), memberRequest.getId(), FriendStatus.FRIEND_ACCEPTED)).thenReturn(Optional.of(friendToMember));

    //when
    boolean deleteResponse = friendService.deleteFriend(memberRequest.getId());

    //then
    assertThat(deleteResponse).isTrue();
    verify(memberRepository, times(1)).findById(anyLong());
    verify(friendRepository, times(2)).findByMemberRequestIdAndFriendIdAndStatus(any(),any(),any());
  }

  @Test
  @DisplayName("친구 삭제 - 실패 : 친구를 회원 DB에서 찾을 수 없음")
  void deleteFriend_fail_not_found_member() {
    //given
    Long memberRequestId=1L;

    given(memberRepository.findById(memberRequestId)).willReturn(Optional.empty());

    //when
    //then
    Assertions.assertThrows(MemberException.class,()-> friendService.deleteFriend(memberRequestId));
    verify(memberRepository, times(1)).findById(anyLong());
    verify(friendRepository, times(0)).findByMemberRequestIdAndFriendIdAndStatus(any(),any(),any());
  }

  @Test
  @DisplayName("친구 삭제 - 실패 : 친구 목록에서 ACCEPT 인 상태를 찾을 수 없음")
  void deleteFriend_fail_not_found_accepted_memberRequest() {
    //given
    Long memberRequestId=1L;
    Member friend = CAT.createMember();

    given(memberRepository.findById(memberRequestId)).willReturn(Optional.of(friend));
    given(friendRepository.findByMemberRequestIdAndFriendIdAndStatus(any(),any(),any())).willReturn(Optional.empty());

    //when
    //then
    Assertions.assertThrows(FriendException.class,()-> friendService.deleteFriend(memberRequestId));
    verify(memberRepository, times(1)).findById(anyLong());
    verify(friendRepository, times(1)).findByMemberRequestIdAndFriendIdAndStatus(any(),any(),any());
  }

}