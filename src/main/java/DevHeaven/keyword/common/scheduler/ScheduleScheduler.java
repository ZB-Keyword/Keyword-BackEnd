package DevHeaven.keyword.common.scheduler;

import DevHeaven.keyword.domain.schedule.entity.Schedule;
import DevHeaven.keyword.domain.schedule.repository.ScheduleRepository;
import DevHeaven.keyword.domain.schedule.type.ScheduleStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleScheduler {
    private final ScheduleRepository scheduleRepository;
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void alterScheduleStatus() {
        log.info("일정 종료 하루뒤에 일정 상태 변경");

        List<Schedule> scheduleList =
                scheduleRepository.findScheduleEnd();


        scheduleList
                .forEach(schedule ->
                        schedule.setStatus(ScheduleStatus.END));

    }
}
