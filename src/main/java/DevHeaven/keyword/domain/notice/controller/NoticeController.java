package DevHeaven.keyword.domain.notice.controller;

import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.notice.dto.response.NoticeResponse;
import DevHeaven.keyword.domain.notice.service.NoticeService;
import java.io.IOException;
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

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NoticeController {

  private final NoticeService noticeService;

  @GetMapping(value = "/subscribe", produces = "text/event-stream")
  @ResponseBody
  public ResponseEntity<String> subscribe(@AuthenticationPrincipal MemberAdapter memberAdapter)
      throws IOException {

    return noticeService.subscribe(memberAdapter);
  }

  @DeleteMapping("/{noticeId}")
  public ResponseEntity<Boolean> deleteNotification(
      @AuthenticationPrincipal final MemberAdapter memberAdapter,
      @PathVariable final Long noticeId
  ) {
    return ResponseEntity.ok(noticeService.deleteNotice(memberAdapter, noticeId));

  }

  @GetMapping
  public ResponseEntity<Page<NoticeResponse>> getNoticeList(
      @AuthenticationPrincipal final MemberAdapter memberAdapter,
      Pageable pageable) {

    return ResponseEntity.ok(noticeService.getNoticeList(memberAdapter, pageable));

  }
}
