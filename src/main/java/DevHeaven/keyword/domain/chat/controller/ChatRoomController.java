package DevHeaven.keyword.domain.chat.controller;

import DevHeaven.keyword.domain.chat.dto.response.ChatResponse;
import DevHeaven.keyword.domain.chat.dto.response.ChatRoomListResponse;
import DevHeaven.keyword.domain.chat.service.ChatRoomService;
import DevHeaven.keyword.domain.chat.service.ChatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Boolean> createChatRoom(final Long scheduleId) {
        return ResponseEntity.ok(
            chatRoomService.createChatRoom(scheduleId));
    }

    @GetMapping("/room")
    public ResponseEntity<Page<ChatRoomListResponse>> getChatRoomList(
        Pageable pageable) {
        //파라미터로 UserAdapter 추가하고 서비스 단에 넘겨줘야함
        return ResponseEntity.ok(
            chatRoomService.getChatRoomList(pageable));
    }

    @GetMapping("/room/{chatRoomId}")
    public ResponseEntity<List<ChatResponse>> enterChatRoom(
        @PathVariable Long chatRoomId,
        Model model) {
        //파라미터로 UserAdapter 추가해야함
        //서비스 단에 넘겨줘야함
        return ResponseEntity.ok(
            chatRoomService.enterChatRoom(chatRoomId));
    }

}
