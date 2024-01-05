package DevHeaven.keyword.domain.chat.service;

import static DevHeaven.keyword.common.exception.type.ErrorCode.SCHEDULE_NOT_FOUND;
import static DevHeaven.keyword.domain.chat.type.ChatRoomStatus.VALID;

import DevHeaven.keyword.common.exception.ScheduleException;
import DevHeaven.keyword.domain.chat.dto.ChatDTO;
import DevHeaven.keyword.domain.chat.dto.ChatRoomListResponse;
import DevHeaven.keyword.domain.chat.entity.ChatRoom;
import DevHeaven.keyword.domain.chat.repository.ChatRoomRepository;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.schedule.entity.Schedule;
import DevHeaven.keyword.domain.schedule.entity.ScheduleFriend;
import DevHeaven.keyword.domain.schedule.repository.ScheduleFriendRepository;
import DevHeaven.keyword.domain.schedule.repository.ScheduleRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleFriendRepository scheduleFriendRepository;

    /**
     * 채팅방 생성 + 멤버들이 채팅방 구독
     *
     * @param
     */
    public void createChatRoom(final Long scheduleId) {

        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() ->
                new ScheduleException(SCHEDULE_NOT_FOUND));

        // 채팅방 생성
        chatRoomRepository.save(
            ChatRoom.builder()
                .schedule(schedule)
                .member(schedule.getMember())
                .status(VALID)
                .build()
        );

        //멤버들이 채팅방 구독
        //일정 수정애서 멤버 수정할 때도 되어야 함...

//        ChatRoomDTO room = ChatRoomDTO.create(name);
//
//        chatRoomRepository.save(room.toEntity());
    }

    /**
     * 회원 자신의 채팅방 목록 조회
     */
    public Page<ChatRoomListResponse> getChatRoom(Pageable pageable) {

//          UserAdapter 통해 member entity 추출 로직 필요
        Member member = new Member();

//          회원 정보로 ScheduleFriend 레코드 찾는 로직 필요
//          일정 참여 친구 목록 테이블에서 일정 참여친구로 들어있는 나를 뽑아올 수 있음
        List<ScheduleFriend> scheduleFriendList =
            scheduleFriendRepository.findByMember(member);

//          "내"가 포함된 일정 목록에서 일정 아이디로 채팅방 조회
        List<ChatRoom> chatRoomList = new ArrayList<>();

        for (ScheduleFriend sf : scheduleFriendList) {
            chatRoomList.add(
                chatRoomRepository.findBySchedule(sf.getSchedule()));
        }

        //해당 일정마다의 일정 참여 친구 목록이 있어야함
        return new PageImpl<>(chatRoomList)
            .map(ChatRoom::from);
    }

    /**
     * 채팅방 입장
     */
    public List<ChatDTO> enterChatRoom(Long chatRoomId) {
        chatRoomRepository.findById(chatRoomId);
        return null;
    }
}
