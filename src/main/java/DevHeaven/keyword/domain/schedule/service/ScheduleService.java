package DevHeaven.keyword.domain.schedule.service;

import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.common.exception.ScheduleException;
import DevHeaven.keyword.common.exception.type.ErrorCode;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import DevHeaven.keyword.domain.member.service.MemberService;
import DevHeaven.keyword.domain.schedule.dto.request.ScheduleCreateRequest;
import DevHeaven.keyword.domain.schedule.dto.request.ScheduleFriendRequest;
import DevHeaven.keyword.domain.schedule.dto.response.ScheduleCreateResponse;
import DevHeaven.keyword.domain.schedule.dto.response.ScheduleListResponse;
import DevHeaven.keyword.domain.schedule.entity.Schedule;
import DevHeaven.keyword.domain.schedule.repository.ScheduleRepository;
import DevHeaven.keyword.domain.schedule.type.ScheduleStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static DevHeaven.keyword.common.exception.type.ErrorCode.EMAIL_NOT_FOUND;
import static DevHeaven.keyword.common.exception.type.ErrorCode.SCHEDULE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;
    private final MemberService memberService;

    public Page<ScheduleListResponse> getScheduleList(
            MemberAdapter memberAdapter,
            Pageable pageable
    ) {
        Member member = getMemberByEmail(memberAdapter.getEmail());
        List<Schedule> scheduleList =
                scheduleRepository.getScheduleListByMember(member.getMemberId());

        return new PageImpl<>(scheduleList.stream()
                .map(Schedule::from)
                .collect(Collectors.toList()), pageable, scheduleList.size());
    }

    private Member getMemberByEmail(final String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(EMAIL_NOT_FOUND));
    }


    public ScheduleCreateResponse createSchedule(ScheduleCreateRequest request,
                                                 MemberAdapter memberAdapter) {

        Member member = getMemberByEmail(memberAdapter.getEmail());

        // friend 상태 확인
        for (ScheduleFriendRequest friend : request.getScheduleFriendList()) {
            Member friendInfomation = memberRepository.findById(friend.getMemberId())
                    .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

            memberService.validateMemberByStatus(friendInfomation);

        }

        Schedule schedule = Schedule.builder()
                .title(request.getTitle())
                .contents(request.getContents())
                .scheduleAt(request.getScheduleAt())
                .locationExplanation(request.getLocationExplanation())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .status(ScheduleStatus.ONGOING)
                .remindAt(request.getRemindAt())
                .member(member)
                .friendList(toMemberList(request.getScheduleFriendList()))
                .build();

        scheduleRepository.save(schedule);

        return ScheduleCreateResponse.builder().scheduleId(schedule.getScheduleId()).build();
    }

    private List<Member> toMemberList(List<ScheduleFriendRequest> scheduleFriendRequestList) {
        return scheduleFriendRequestList.stream()
                .map(x -> memberRepository.findById(x.getMemberId()).get())
                .collect(Collectors.toList());
    }

    public boolean deleteSchedule(MemberAdapter memberAdapter, final Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(SCHEDULE_NOT_FOUND));

        schedule.setScheduleStatus();

        return true;
    }
}
