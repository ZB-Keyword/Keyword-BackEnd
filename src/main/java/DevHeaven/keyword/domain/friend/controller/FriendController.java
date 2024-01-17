package DevHeaven.keyword.domain.friend.controller;

import DevHeaven.keyword.domain.friend.entity.Friend;
import DevHeaven.keyword.domain.friend.service.FriendService;
import DevHeaven.keyword.domain.friend.type.FriendState;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

  private final FriendService friendService;

  // TODO : 임시 메서드 명칭 (메서드 명이랑 DTO 바꿔주세요)
  @GetMapping(params = {"keyword"})
  public ResponseEntity<Page<Member>> searchFriend(
      @AuthenticationPrincipal final MemberAdapter memberAdapter,
      @RequestParam final String keyword,
      final Pageable pageable) {
    return ResponseEntity.ok(friendService.searchFriend(memberAdapter, keyword, pageable));
  }

  @PostMapping("/{memberId}")
  public ResponseEntity<Boolean> requestFriend(
      @AuthenticationPrincipal final MemberAdapter memberAdapter,
      @PathVariable final Long memberId) {
    return ResponseEntity.ok(friendService.requestFriend(memberAdapter, memberId));
  }

  @DeleteMapping("/{memberReqId}")
  public ResponseEntity<Boolean> deleteFriend(
      @AuthenticationPrincipal final MemberAdapter memberAdapter,
      @PathVariable(name = "memberReqId") final Long memberRequestId) {
    return ResponseEntity.ok(friendService.deleteFriend(memberAdapter, memberRequestId));
  }
}