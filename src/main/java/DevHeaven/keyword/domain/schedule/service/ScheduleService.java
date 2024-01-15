package DevHeaven.keyword.domain.schedule.service;

import static DevHeaven.keyword.common.exception.type.ErrorCode.EMAIL_NOT_FOUND;
import static DevHeaven.keyword.common.exception.type.ErrorCode.MEMBER_NOT_FOUND;

import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import DevHeaven.keyword.domain.member.service.MemberService;
import DevHeaven.keyword.domain.schedule.dto.request.ScheduleCreateRequest;
import DevHeaven.keyword.domain.schedule.dto.response.ScheduleCreateResponse;
import DevHeaven.keyword.domain.schedule.entity.Schedule;
import DevHeaven.keyword.domain.schedule.repository.ScheduleRepository;
import DevHeaven.keyword.domain.schedule.type.ScheduleStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleService {

  private final ScheduleRepository scheduleRepository;
  private final MemberRepository memberRepository;
  private final MemberService memberService;

  public ScheduleCreateResponse createSchedule(ScheduleCreateRequest request,
      MemberAdapter memberAdapter) {

    Member member = memberRepository.findByEmail(memberAdapter.getEmail())
        .orElseThrow(() -> new MemberException(EMAIL_NOT_FOUND));

    // friend 상태 확인

    for (Member friend : request.getScheduleFriendList()) {
      Member friendInfomation = memberRepository.findById(friend.getMemberId())
          .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

      memberService.validateMemberByStatus(friendInfomation);

    }

    Schedule schedule = Schedule.builder()
        .member(member)
        .title(request.getTitle())
        .contents(request.getContents())
        .scheduleAt(request.getScheduleAt())
        .locationExplanation(request.getLocationExplanation())
        .latitude(request.getLatitude())
        .longitude(request.getLongitude())
        .status(ScheduleStatus.ONGOING)
        .remindAt(request.getRemindAt())
        .schduleFriendList(request.getScheduleFriendList())
        .build();

    scheduleRepository.save(schedule);

    return ScheduleCreateResponse.builder().scheduleId(schedule.getScheduleId()).build();
  }
}
