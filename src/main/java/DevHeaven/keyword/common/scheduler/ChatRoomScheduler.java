package DevHeaven.keyword.common.scheduler;

import DevHeaven.keyword.domain.chat.repository.ChatRoomRepository;
import DevHeaven.keyword.domain.schedule.entity.Schedule;
import DevHeaven.keyword.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static DevHeaven.keyword.domain.chat.type.ChatRoomStatus.INVALID;
import static DevHeaven.keyword.domain.chat.type.ChatRoomStatus.VALID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomScheduler {
    private final ScheduleRepository scheduleRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void alterChatroomStatus() {
        log.info("일정 종료 3일 뒤의 채팅방 상태 변경 스켸줄러 실행");

        List<Schedule> scheduleList =
                scheduleRepository.findScheduleEndThreeDay();

        log.info(scheduleList.toString());
        scheduleList
                .forEach(schedule ->
                        chatRoomRepository.findByScheduleAndStatus(schedule, VALID)
                                .ifPresent(chatRoom -> chatRoom.setStatus(INVALID)));
    }
}
