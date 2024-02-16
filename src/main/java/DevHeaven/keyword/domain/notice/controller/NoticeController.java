package DevHeaven.keyword.domain.notice.controller;

import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.notice.dto.response.NoticeResponse;
import DevHeaven.keyword.domain.notice.service.NoticeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
  private final ObjectMapper objectMapper;



  @GetMapping(value = "/subscribe", produces = "text/event-stream")
  @ResponseBody
  public ResponseEntity<String> subscribe(@AuthenticationPrincipal MemberAdapter memberAdapter) throws IOException {
    try {
      SseEmitter subscribe = noticeService.subscribe(memberAdapter.getEmail());
      Map<String, Object> responseMap = new HashMap<>();
      responseMap.put("success", true);
      responseMap.put("emitter", subscribe);

      // ObjectMapper를 사용하여 JSON 문자열로 직접 변환
      String jsonResponse = objectMapper.writeValueAsString(responseMap);

      // ResponseEntity에 JSON 문자열을 넣어 반환
      return ResponseEntity.ok(jsonResponse);
    } catch (IOException e) {
      Map<String, Object> errorMap = new HashMap<>();
      errorMap.put("success", false);
      errorMap.put("error", "Failed to subscribe");

      // ObjectMapper를 사용하여 JSON 문자열로 직접 변환
      String jsonError = objectMapper.writeValueAsString(errorMap);

      // Internal Server Error로 응답
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonError);
    }
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
