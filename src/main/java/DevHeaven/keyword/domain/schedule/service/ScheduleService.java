package DevHeaven.keyword.domain.schedule.service;

import DevHeaven.keyword.domain.schedule.dto.request.ScheduleCreateRequest;
import DevHeaven.keyword.domain.schedule.dto.response.ScheduleCreateResponse;
import DevHeaven.keyword.domain.schedule.entity.Schedule;
import DevHeaven.keyword.domain.schedule.repository.ScheduleRepository;
import DevHeaven.keyword.domain.schedule.type.ScheduleStatus;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleService {

  private final ScheduleRepository scheduleRepository;

  public ScheduleCreateResponse createChedule(ScheduleCreateRequest request) {

    Schedule schedule = Schedule.builder()
        .title(request.getTitle())
        .contents(request.getContents())
        .scheduleAt(request.getScheduleAt())
        .locationExplanation(request.getLocationExplanation())
        .latitude(request.getLatitude())
        .longitude(request.getLongitude())
        .status(ScheduleStatus.EXIST)
        .createdAt(LocalDateTime.now())
        .remindAt(request.getRemindAt())
        .createdAt(LocalDateTime.now())
        .schduleFriendList(request.getScheduleFriendList())
        .build();


    scheduleRepository.save(schedule);


    return ScheduleCreateResponse.builder().isScheduleRequest(true).build();
  }
}
