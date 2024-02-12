package DevHeaven.keyword.domain.notice.service;

import DevHeaven.keyword.domain.notice.dto.NoticeEvent;
import DevHeaven.keyword.domain.notice.type.NoticeType;
import DevHeaven.keyword.domain.schedule.service.ScheduleService;
import org.springframework.stereotype.Service;

@Service
public class ServiceCallingSendMethod {

  private final ScheduleService scheduleService;
  private final NoticeService noticeService;

  public ServiceCallingSendMethod(ScheduleService scheduleService, NoticeService noticeService) {
    this.scheduleService = scheduleService;
    this.noticeService = noticeService;
  }

  public void callSendMethod(Long scheduleId, Long memberId, NoticeType noticeType) {
    // 이벤트 발생시키기
    scheduleService.sendNotice(scheduleId, memberId, noticeType);

    // 만약 이벤트를 동기적으로 처리하려면 아래 코드를 주석 해제
    noticeService.handleNoticeEvent(
        NoticeEvent.builder()
            .id(scheduleId)
            .memberId(memberId)
            .noticeType(noticeType)
            .build());
  }
}
