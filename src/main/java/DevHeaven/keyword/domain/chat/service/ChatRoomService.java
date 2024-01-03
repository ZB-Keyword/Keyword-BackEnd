package DevHeaven.keyword.domain.chat.service;

import DevHeaven.keyword.domain.chat.dto.ChatRoomDTO;
import DevHeaven.keyword.domain.chat.entity.ChatRoom;
import DevHeaven.keyword.domain.chat.repository.ChatRoomRepository;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.schedule.entity.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    /**
     * 채팅방 생성
     * @param name
     */
    public void createChatRoom(String name) {
//        ChatRoomDTO room = ChatRoomDTO.create(name);
//
//        chatRoomRepository.save(room.toEntity());
    }

    /**
     * 회원 자신의 채팅방 목록 조회
     */
    public Page<ChatRoomDTO> getChatRoom(Pageable pageable) {
        //UserAdapter 통해 member entity 추출 로직 필요
        Member member = new Member();

        //회원 정보로 Schedule entity 찾는 로직 필요
        Schedule schedule = new Schedule();

        return chatRoomRepository.findByMember(member, pageable)
            .map(ChatRoom::from);
    }
}
