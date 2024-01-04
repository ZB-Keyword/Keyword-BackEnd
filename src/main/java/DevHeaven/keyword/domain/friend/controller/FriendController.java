package DevHeaven.keyword.domain.friend.controller;

import DevHeaven.keyword.domain.friend.dto.response.FriendDeleteResponse;
import DevHeaven.keyword.domain.friend.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

  private final FriendService friendService;

  // TODO : 시큐리티 적용후 @AuthPrincipal 파라미터 추가 예정
  @DeleteMapping("/{memberReqId}")
  public ResponseEntity <FriendDeleteResponse> deleteFriend(@PathVariable(required = true,name = "memberReqId") final Long memberRequestId){
    return ResponseEntity.ok(friendService.deleteFriend(memberRequestId));
  }
}