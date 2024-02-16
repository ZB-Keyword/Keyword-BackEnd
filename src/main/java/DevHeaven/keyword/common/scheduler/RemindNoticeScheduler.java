package DevHeaven.keyword.common.scheduler;

import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import DevHeaven.keyword.domain.notice.dto.NoticeEvent;
import DevHeaven.keyword.domain.notice.type.NoticeType;
import DevHeaven.keyword.domain.schedule.dto.response.ScheduleCreateResponse;
import DevHeaven.keyword.domain.schedule.entity.Schedule;
import DevHeaven.keyword.domain.schedule.repository.ScheduleRepository;
import DevHeaven.keyword.domain.schedule.type.ScheduleStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RemindNoticeScheduler {

  private final MemberRepository memberRepository;
  private final ScheduleRepository scheduleRepository;
  private final ApplicationEventPublisher applicationEventPublisher;

  //@Scheduled(cron = "0 0 */1 * * *") // 매시간 실행
  @Transactional
  public Object remindNotice() {
    List<Schedule> scheduleList = scheduleRepository.findAllByStatusAndScheduleAtBetween(
        ScheduleStatus.ONGOING, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

    for (Schedule schedule : scheduleList) {
      if (
          schedule.getScheduleAt().minusHours(schedule.getRemindAt()).isEqual(LocalDateTime.now()) ||
          schedule.getScheduleAt().minusHours(schedule.getRemindAt()).isBefore(LocalDateTime.now())) {
        for (Member friend : schedule.getFriendList()) {
          sendNotice(friend, friend.getMemberId());
        }
      }

      return schedule.getScheduleId();
    }
    return null;
  }


  private void sendNotice(final Member member, final Long memberId) {
    applicationEventPublisher.publishEvent(
        NoticeEvent.builder()
            .id(member.getMemberId())
            .memberId(memberId)
            .noticeType(NoticeType.SCHEDULE_CALCEL)
            .build()
    );
  }
}
