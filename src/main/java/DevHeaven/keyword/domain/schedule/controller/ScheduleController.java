package DevHeaven.keyword.domain.schedule.controller;

import DevHeaven.keyword.domain.schedule.dto.request.ScheduleCreateRequest;
import DevHeaven.keyword.domain.schedule.dto.response.ScheduleCreateResponse;
import DevHeaven.keyword.domain.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ScheduleController {

  public final ScheduleService scheduleService;

  @PostMapping("/schedules")
  public ResponseEntity<ScheduleCreateResponse> createSchedule(
      @RequestBody ScheduleCreateRequest request) {

    scheduleService.createChedule(request);

    return ResponseEntity.ok(ScheduleCreateResponse.builder().isScheduleRequest(true).build());
  }
}
