package DevHeaven.keyword.domain.notice.controller;

import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.notice.service.NoticeService;
import DevHeaven.keyword.domain.notice.service.ServiceCallingSendMethod;
import DevHeaven.keyword.domain.notice.type.NoticeType;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/notification")
public class NoticeController {
  private final NoticeService noticeService;
  private final ServiceCallingSendMethod serviceCallingSendMethod;

  public NoticeController(NoticeService noticeService,
      ServiceCallingSendMethod serviceCallingSendMethod) {
    this.noticeService = noticeService;
    this.serviceCallingSendMethod = serviceCallingSendMethod;
  }

  @GetMapping(value = "/subscribe", produces = "text/event-stream")
  public SseEmitter subscribe(@AuthenticationPrincipal MemberAdapter memberAdapter) throws IOException {
    return noticeService.subscribe(memberAdapter.getEmail());
  }
  @GetMapping("/callSendMethod")
  public ResponseEntity<String> callSendMethod(@RequestParam Long scheduleId, @RequestParam Long memberId) {
    serviceCallingSendMethod.callSendMethod(scheduleId, memberId, NoticeType.SCHEDULE_CALCEL);
    return ResponseEntity.ok("Send method called!");
  }

}
