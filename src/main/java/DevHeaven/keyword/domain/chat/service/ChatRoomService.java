package DevHeaven.keyword.domain.chat.service;

import DevHeaven.keyword.common.exception.ChatException;
import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.common.exception.ScheduleException;
import DevHeaven.keyword.domain.chat.dto.response.ChatResponse;
import DevHeaven.keyword.domain.chat.dto.response.ChatRoomListResponse;
import DevHeaven.keyword.domain.chat.entity.Chat;
import DevHeaven.keyword.domain.chat.entity.ChatRoom;
import DevHeaven.keyword.domain.chat.repository.ChatRepository;
import DevHeaven.keyword.domain.chat.repository.ChatRoomRepository;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import DevHeaven.keyword.domain.schedule.entity.Schedule;
import DevHeaven.keyword.domain.schedule.repository.ScheduleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static DevHeaven.keyword.common.exception.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;

    public boolean createChatRoom(
            final MemberAdapter memberAdapter,
            final Long scheduleId) {

        Member member = getMemberByEmail(memberAdapter.getEmail());

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(SCHEDULE_NOT_FOUND));

        validateMemberSchedule(member, schedule);

        chatRoomRepository.save(ChatRoom.createRoom(schedule));

        return true;
    }

    private void validateMemberSchedule(
            final Member member,
            final Schedule schedule) {

        if (!schedule.getFriendList().contains(member)) {
            throw new ScheduleException(SCHEDULE_MEMBER_UNMATCHED);
        }
    }

    public Page<ChatRoomListResponse> getChatRoomList(
            final MemberAdapter memberAdapter,
            final Pageable pageable) {

        Member member = getMemberByEmail(memberAdapter.getEmail());

        List<Schedule> scheduleList =
                scheduleRepository.getScheduleListByMember(member.getMemberId(), pageable);

        List<ChatRoom> chatRoomList = new ArrayList<>();

        for (Schedule schedule : scheduleList) {
            chatRoomList.add(
                    chatRoomRepository.findBySchedule(schedule));
        }

        return new PageImpl<>(chatRoomList.stream()
                .map(ChatRoom::from)
                .collect(Collectors.toList()), pageable, chatRoomList.size());
    }

    private Member getMemberByEmail(
            final String email) {

        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(EMAIL_NOT_FOUND));
    }

    public List<ChatResponse> enterChatRoom(
            final MemberAdapter memberAdapter,
            final Long chatRoomId) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatException(CHATROOM_NOT_FOUND));
        log.info(chatRoom.getChatRoomId().toString());

        return chatRepository.findByChatRoom(chatRoom)
                .stream().map(Chat::from).collect(Collectors.toList());
    }
}
