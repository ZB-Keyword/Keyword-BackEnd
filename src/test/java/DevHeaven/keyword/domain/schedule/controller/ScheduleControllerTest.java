package DevHeaven.keyword.domain.schedule.controller;

import DevHeaven.keyword.domain.schedule.dto.response.ScheduleListResponse;
import DevHeaven.keyword.domain.schedule.service.ScheduleService;
import DevHeaven.keyword.domain.schedule.type.ScheduleStatus;
import org.junit.Ignore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.restdocs.RestDocumentationExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;


@ExtendWith({RestDocumentationExtension.class , MockitoExtension.class})
class ScheduleControllerTest {
    @Mock
    private ScheduleService scheduleService;

    @InjectMocks
    private ScheduleController scheduleController;

    @Test
    @DisplayName("일정 목록 테스트")
    @Ignore
    void getScheduleList() {
        ScheduleListResponse scheduleListResponse =
                ScheduleListResponse
                        .builder()
                        .scheduleId(1L)
                        .title("크리스마스 일정")
                        .scheduleDateTime(LocalDateTime.now())
                        .locationExplanation("홍대입구역 4번출구 앞")
                        .status(ScheduleStatus.ONGOING)
                        .build();
//        Page<ScheduleListResponse> pageResult =
//                new PageImpl<>(Collections.singletonList(ScheduleListResponse))
//        given(scheduleService.getScheduleList())
//                .willReturn(P)
    }
}