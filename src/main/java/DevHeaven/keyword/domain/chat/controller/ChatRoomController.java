package DevHeaven.keyword.domain.chat.controller;

import DevHeaven.keyword.domain.chat.dto.response.ChatResponse;
import DevHeaven.keyword.domain.chat.dto.response.ChatRoomListResponse;
import DevHeaven.keyword.domain.chat.service.ChatRoomService;
import DevHeaven.keyword.domain.chat.service.ChatService;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<Boolean> createChatRoom(
        @PathVariable final Long scheduleId) {

        return ResponseEntity.ok(
            chatRoomService.createChatRoom(scheduleId));
    }

    @GetMapping("/room")
    public ResponseEntity<Page<ChatRoomListResponse>> getChatRoomList(
        @AuthenticationPrincipal MemberAdapter memberAdapter,
        Pageable pageable) {

        return ResponseEntity.ok(
            chatRoomService.getChatRoomList(memberAdapter, pageable));
    }

    @GetMapping("/room/{chatRoomId}")
    public ResponseEntity<List<ChatResponse>> enterChatRoom(
        @PathVariable Long chatRoomId,
        @AuthenticationPrincipal MemberAdapter memberAdapter,
        Model model) {

        return ResponseEntity.ok(
            chatRoomService.enterChatRoom(memberAdapter, chatRoomId));
    }

}
