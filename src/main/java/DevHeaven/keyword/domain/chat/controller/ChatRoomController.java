package DevHeaven.keyword.domain.chat.controller;

import DevHeaven.keyword.domain.chat.dto.ChatDTO;
import DevHeaven.keyword.domain.chat.dto.ChatRoomListResponse;
import DevHeaven.keyword.domain.chat.service.ChatRoomService;
import DevHeaven.keyword.domain.chat.service.ChatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
@Slf4j
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final ChatService chatService;

    /**
     * 일정 생성 버튼 누름 동시에
     * 채팅방 생성
     * 필요 정보 파라미터 : 일정 id
     */
    @PostMapping("/room/{scheduleId}")
    public void createChatRoom(final Long scheduleId, Pageable pageable) {
        chatRoomService.createChatRoom(scheduleId, pageable);
    }

    /**
     * 자신의 채팅방 목록 조회
     */
    @GetMapping
    public Page<ChatRoomListResponse> getChatRoom(Pageable pageable) {
        //파라미터로 UserAdapter 추가하고
        //서비스 단에 넘겨줘야함
        return chatRoomService.getChatRoom(pageable);
    }

    /**
     * 채팅방 조회
     */
    @GetMapping("/{chatRoomId}")
    public List<ChatDTO> enterChatRoom(
        @PathVariable Long chatRoomId,
        Model model) {
        //파라미터로 UserAdapter 추가해야함
        //서비스 단에 넘겨줘야함
        return chatRoomService.enterChatRoom(chatRoomId);
    }

}
