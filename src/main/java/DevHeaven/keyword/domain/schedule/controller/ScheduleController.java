package DevHeaven.keyword.domain.schedule.controller;

import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.schedule.dto.request.ScheduleCreateRequest;
import DevHeaven.keyword.domain.schedule.dto.response.ScheduleCreateResponse;
import DevHeaven.keyword.domain.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ScheduleController {

  public final ScheduleService scheduleService;


  // 스케줄 생성
  @PostMapping("/schedules")
  public ResponseEntity<ScheduleCreateResponse> createSchedule(
      @RequestBody ScheduleCreateRequest request,
      @AuthenticationPrincipal MemberAdapter memberAdapter) {

    return ResponseEntity.ok(scheduleService.createSchedule(request, memberAdapter));
  }
}
