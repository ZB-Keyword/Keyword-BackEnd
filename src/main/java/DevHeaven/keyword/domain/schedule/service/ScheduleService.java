package DevHeaven.keyword.domain.schedule.service;

import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.common.exception.ScheduleException;
import DevHeaven.keyword.common.exception.type.ErrorCode;
import DevHeaven.keyword.domain.chat.entity.ChatRoom;
import DevHeaven.keyword.domain.chat.repository.ChatRoomRepository;
import DevHeaven.keyword.domain.chat.type.ChatRoomStatus;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import DevHeaven.keyword.domain.notice.dto.NoticeEvent;
import DevHeaven.keyword.domain.notice.type.NoticeType;
import DevHeaven.keyword.domain.schedule.dto.request.ScheduleCreateRequest;
import DevHeaven.keyword.domain.schedule.dto.ScheduleFriend;
import DevHeaven.keyword.domain.schedule.dto.request.ScheduleFriendRequest;
import DevHeaven.keyword.domain.schedule.dto.request.ScheduleModifyRequest;
import DevHeaven.keyword.domain.schedule.dto.response.ScheduleCreateResponse;
import DevHeaven.keyword.domain.schedule.dto.response.ScheduleDetailResponse;
import DevHeaven.keyword.domain.schedule.dto.response.ScheduleListResponse;
import DevHeaven.keyword.domain.schedule.dto.response.ScheduleModifyResponse;
import DevHeaven.keyword.domain.schedule.entity.Schedule;
import DevHeaven.keyword.domain.schedule.repository.ScheduleRepository;
import DevHeaven.keyword.domain.schedule.type.ScheduleStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static DevHeaven.keyword.common.exception.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public Page<ScheduleListResponse> getScheduleList(
            final MemberAdapter memberAdapter,
            Pageable pageable
    ) {
        Member member = getMemberByEmail(memberAdapter.getEmail());


        List<Schedule> scheduleList =
                scheduleRepository.getScheduleListByMember(member.getMemberId(), pageable);

        return new PageImpl<>(scheduleList.stream()
                .map(Schedule::toScheduleList)
                .collect(Collectors.toList()), pageable, scheduleList.size());
    }

    public ScheduleCreateResponse createSchedule(
            final ScheduleCreateRequest request,
            final MemberAdapter memberAdapter) {

        Member member = getMemberByEmail(memberAdapter.getEmail());

        request.getScheduleFriendList().add(
                new ScheduleFriend(
                        member.getMemberId(), member.getName(), member.getProfileImageFileName(), member.getEmail())
        );

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

        return ScheduleCreateResponse.builder()
                .scheduleId(schedule.getScheduleId())
                .build();
    }

    private List<Member> toMemberList(
            final List<ScheduleFriend> scheduleFriendList) {

        return scheduleFriendList.stream()
                .map(sf -> memberRepository.findById(sf.getMemberId()).get())
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean deleteSchedule(
            final MemberAdapter memberAdapter,
            final Long scheduleId) {

        Member member = getMemberByEmail(memberAdapter.getEmail());

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(SCHEDULE_NOT_FOUND));

        validateOrganizerSchedule(member, schedule);

        schedule.setStatus(ScheduleStatus.DELETE);

        ChatRoom chatRoom = chatRoomRepository.findBySchedule(schedule);
        chatRoom.setStatus(ChatRoomStatus.INVALID);

        //todo 스케쥴 삭제 알림
        sendNotice(scheduleId, member.getMemberId(), NoticeType.SCHEDULE_CALCEL);
        return true;
    }

    public void sendNotice(Long scheduleId, Long memberId, NoticeType noticeType) {
        //이벤트 등록
        applicationEventPublisher.publishEvent(
            NoticeEvent.builder()
                .id(scheduleId)
                .memberId(memberId)
                .noticeType(noticeType)
                .build()
        );
    }

    private void validateOrganizerSchedule(
            final Member member,
            final Schedule schedule) {

        Schedule savedSchedule =
                scheduleRepository.findByMemberAndScheduleId(member, schedule.getScheduleId())
                        .orElseThrow(() -> new ScheduleException(SCHEDULE_NOT_FOUND));

        if (!Objects.equals(savedSchedule.getScheduleId(), schedule.getScheduleId())) {
            throw new ScheduleException(MEMBER_NOT_ORGANIZER);
        }
    }



    public ScheduleDetailResponse getScheduleDetail(
            final MemberAdapter memberAdapter,
            final Long scheduleId,
            final Long noticeId) {

        Member member = getMemberByEmail(memberAdapter.getEmail());

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(SCHEDULE_NOT_FOUND));

        return schedule.toScheduleDetail();
    }

    private Member getMemberByEmail(final String email) {

        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(EMAIL_NOT_FOUND));
    }

    @Transactional
    public ScheduleModifyResponse modifySchedule(
            final MemberAdapter memberAdapter,
            final Long scheduleId,
            final ScheduleModifyRequest request) {

        final Member member = getMemberByEmail(memberAdapter.getEmail());

        final Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(SCHEDULE_NOT_FOUND));

        if (schedule.getMember().getMemberId() != member.getMemberId()) {
            throw new ScheduleException(ErrorCode.MEMBER_NOT_ORGANIZER);
        }

        final List <Member> scheduleFriendList = friendRequestToMemberList(request.getScheduleFriendList());
        scheduleFriendList.add(member);

        schedule.updateSchedule(request, scheduleFriendList);

        return ScheduleModifyResponse.builder()
                .scheduleId(scheduleId)
                .remindAt(schedule.getRemindAt())
                .build();
    }

    private List<Member> friendRequestToMemberList(
        final List<ScheduleFriendRequest> scheduleFriendList) {

        return scheduleFriendList.stream()
            .map(sf -> memberRepository.findById(sf.getMemberId()).get())
            .collect(Collectors.toList());
    }


}