package DevHeaven.keyword.domain.friend.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import DevHeaven.keyword.common.exception.FriendException;
import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.domain.friend.entity.Friend;
import DevHeaven.keyword.domain.friend.repository.FriendRepository;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
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
    Long memberRequestId=1L;
    Member friend = new Member();
    Friend memberToFriend=new Friend();
    Friend friendToMember=new Friend();

    given(memberRepository.findById(anyLong())).willReturn(Optional.of(friend));
    given(friendRepository.findByMemberRequestAndFriendAndStatus(any(),any(),any())).willReturn(Optional.of(memberToFriend));
    given(friendRepository.findByMemberRequestAndFriendAndStatus(any(),any(),any())).willReturn(Optional.of(friendToMember));

    //when
    friendService.deleteFriend(memberRequestId);

    //then
    verify(memberRepository, times(1)).findById(anyLong());
    verify(friendRepository, times(2)).findByMemberRequestAndFriendAndStatus(any(),any(),any());
  }

  @Test
  @DisplayName("친구 삭제 - 실패 : 친구를 회워 DB에서 찾을 수 없음")
  void deleteFriend_fail_not_found_member() throws Exception {
    //given
    Long memberRequestId=1L;

    given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

    //when
    //then
    Assertions.assertThrows(MemberException.class,()-> friendService.deleteFriend(memberRequestId));
    verify(memberRepository, times(1)).findById(anyLong());
    verify(friendRepository, times(0)).findByMemberRequestAndFriendAndStatus(any(),any(),any());
  }

  @Test
  @DisplayName("친구 삭제 - 실패 : 친구 목록에서 ACCEPT 인 상태를 찾을 수 없음")
  void deleteFriend_fail_not_found_accepted_memberRequest() throws Exception {
    //given
    Long memberRequestId=1L;
    Member friend = new Member();

    given(memberRepository.findById(anyLong())).willReturn(Optional.of(friend));
    given(friendRepository.findByMemberRequestAndFriendAndStatus(any(),any(),any())).willReturn(Optional.empty());

    //when
    //then
    Assertions.assertThrows(FriendException.class,()-> friendService.deleteFriend(memberRequestId));
    verify(memberRepository, times(1)).findById(anyLong());
    verify(friendRepository, times(1)).findByMemberRequestAndFriendAndStatus(any(),any(),any());
  }

}