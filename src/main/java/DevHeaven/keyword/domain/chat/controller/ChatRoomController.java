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

    @PostMapping("/room/{scheduleId}")
    public void createChatRoom(final Long scheduleId) {
        chatRoomService.createChatRoom(scheduleId);
    }

    @GetMapping("/room")
    public Page<ChatRoomListResponse> getChatRoomList(Pageable pageable) {
        //파라미터로 UserAdapter 추가하고 서비스 단에 넘겨줘야함
        return chatRoomService.getChatRoomList(pageable);
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
