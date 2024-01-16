package DevHeaven.keyword.domain.friend.controller;

import DevHeaven.keyword.domain.friend.service.FriendService;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

  private final FriendService friendService;

  @PostMapping("/{memberId}")
  public ResponseEntity<Boolean> requestFriend(@AuthenticationPrincipal final MemberAdapter memberAdapter ,@PathVariable final Long memberId){
    return ResponseEntity.ok(friendService.requestFriend(memberAdapter,memberId));
  }

  @DeleteMapping("/{memberReqId}")
  public ResponseEntity <Boolean> deleteFriend(@AuthenticationPrincipal final MemberAdapter memberAdapter ,@PathVariable(name = "memberReqId") final Long memberRequestId){
    return ResponseEntity.ok(friendService.deleteFriend(memberAdapter ,memberRequestId));
  }
}