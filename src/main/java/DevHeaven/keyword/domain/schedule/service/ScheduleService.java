package DevHeaven.keyword.domain.schedule.service;

import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.common.exception.ScheduleException;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import DevHeaven.keyword.domain.schedule.dto.response.ScheduleListResponse;
import DevHeaven.keyword.domain.schedule.entity.Schedule;
import DevHeaven.keyword.domain.schedule.repository.ScheduleRepository;
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

    public boolean deleteSchedule(MemberAdapter memberAdapter, final Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(SCHEDULE_NOT_FOUND));

        schedule.setScheduleStatus();

        return true;
    }
}
