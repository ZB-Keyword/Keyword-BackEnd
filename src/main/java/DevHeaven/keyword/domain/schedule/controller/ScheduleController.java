package DevHeaven.keyword.domain.schedule.controller;

import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.schedule.dto.response.ScheduleDetailResponse;
import DevHeaven.keyword.domain.schedule.dto.response.ScheduleListResponse;
import DevHeaven.keyword.domain.schedule.dto.request.ScheduleCreateRequest;
import DevHeaven.keyword.domain.schedule.dto.response.ScheduleCreateResponse;
import DevHeaven.keyword.domain.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;
  
    @PostMapping
    public ResponseEntity<ScheduleCreateResponse> createSchedule(
        @RequestBody ScheduleCreateRequest request,
        @AuthenticationPrincipal MemberAdapter memberAdapter) {

        return ResponseEntity.ok(
                scheduleService.createSchedule(request, memberAdapter));
    }

    @GetMapping
    public ResponseEntity<Page<ScheduleListResponse>> getScheduleList(
            @AuthenticationPrincipal MemberAdapter memberAdapter,
            Pageable pageable) {
        return ResponseEntity.ok(
                scheduleService.getScheduleList(memberAdapter, pageable));
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Boolean> deleteSchedule(
            @AuthenticationPrincipal MemberAdapter memberAdapter,
            @PathVariable final Long scheduleId
    ) {
        return ResponseEntity.ok(
                scheduleService.deleteSchedule(memberAdapter, scheduleId));
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleDetailResponse> getScheduleDetail(
            @AuthenticationPrincipal MemberAdapter memberAdapter,
            @PathVariable Long scheduleId,
            @RequestParam Long noticeId
    ) {
        return ResponseEntity.ok(
                scheduleService.getScheduleDetail(
                        memberAdapter, scheduleId, noticeId));
    }
}
