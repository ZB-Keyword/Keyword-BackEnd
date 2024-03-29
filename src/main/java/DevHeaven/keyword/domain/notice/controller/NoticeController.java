package DevHeaven.keyword.domain.notice.controller;

import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.notice.dto.response.NoticeListResponse;
import DevHeaven.keyword.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NoticeController {

  private final NoticeService noticeService;

  @GetMapping(value = "/subscribe", produces = "text/event-stream")
  @ResponseBody
  public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal MemberAdapter memberAdapter) {

    return ResponseEntity.ok(noticeService.subscribe(memberAdapter));
  }

  @DeleteMapping("/{noticeId}")
  public ResponseEntity<Boolean> deleteNotification(
      @AuthenticationPrincipal final MemberAdapter memberAdapter,
      @PathVariable final Long noticeId
  ) {
    return ResponseEntity.ok(noticeService.deleteNotice(memberAdapter, noticeId));

  }

  @GetMapping
  public ResponseEntity<Page<NoticeListResponse>> getNoticeList(
      @AuthenticationPrincipal final MemberAdapter memberAdapter,
      Pageable pageable) {

    return ResponseEntity.ok(noticeService.getNoticeList(memberAdapter, pageable));

  }
}
