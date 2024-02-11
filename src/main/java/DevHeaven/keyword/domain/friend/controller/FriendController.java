package DevHeaven.keyword.domain.friend.controller;

import DevHeaven.keyword.domain.friend.dto.request.FriendApproveRequest;
import DevHeaven.keyword.domain.friend.dto.request.FriendListStatusRequest;
import DevHeaven.keyword.domain.friend.dto.response.FriendListResponse;
import DevHeaven.keyword.domain.friend.dto.response.FriendSearchListResponse;
import DevHeaven.keyword.domain.friend.service.ElasticSearchService;
import DevHeaven.keyword.domain.friend.service.FriendService;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

  private final FriendService friendService;
  private final ElasticSearchService elasticSearchService;
  @GetMapping(params = {"keyword"})
  public ResponseEntity<List<FriendSearchListResponse>> searchFriend(@AuthenticationPrincipal final MemberAdapter memberAdapter,
      @RequestParam final String keyword, final Pageable pageable) {
    return ResponseEntity.ok(elasticSearchService.searchMember(keyword, memberAdapter, pageable));
  }

  @GetMapping
  public ResponseEntity<List <FriendListResponse>> getFriendList(@AuthenticationPrincipal final MemberAdapter memberAdapter,
      @RequestParam(name = "friend-state") final FriendListStatusRequest friendState, @RequestParam(required = false) final Long noticeId,
      @PageableDefault final Pageable pageable
      ) {

    return ResponseEntity.ok(friendService.getFriendList(memberAdapter, friendState, noticeId, pageable));
  }
  @PostMapping("/{memberId}")
  public ResponseEntity<Boolean> requestFriend(@AuthenticationPrincipal final MemberAdapter memberAdapter
      ,@PathVariable final Long memberId){
    return ResponseEntity.ok(friendService.requestFriend(memberAdapter,memberId));
  }

  @DeleteMapping("/{memberReqId}")
  public ResponseEntity <Boolean> deleteFriend(@AuthenticationPrincipal final MemberAdapter memberAdapter
      ,@PathVariable(name = "memberReqId") final Long memberRequestId){
    return ResponseEntity.ok(friendService.deleteFriend(memberAdapter ,memberRequestId));
  }

  @PatchMapping("/handle/{memberReqId}")
  public ResponseEntity<Boolean> handleFriendRequest(
          @AuthenticationPrincipal final MemberAdapter memberAdapter,
          @PathVariable(name = "memberReqId") final Long memberReqId,
          @RequestBody final FriendApproveRequest friendApproveRequest) {
    return ResponseEntity.ok(friendService.handleFriendRequest(memberAdapter, memberReqId, friendApproveRequest));
  }


  //이미 db에 있는 데이터 elastic에 넣기 위한임시 메서드
  @PostMapping("/elastic")
  public ResponseEntity<Boolean> saveElastic(@AuthenticationPrincipal final MemberAdapter memberAdapter){
    elasticSearchService.saveAllMembersAsDocuments();
    return ResponseEntity.ok().build();
  }
}