package DevHeaven.keyword.domain.chat.controller;

import DevHeaven.keyword.domain.chat.dto.response.ChatResponse;
import DevHeaven.keyword.domain.chat.dto.response.ChatRoomListResponse;
import DevHeaven.keyword.domain.chat.service.ChatRoomService;
import DevHeaven.keyword.domain.chat.service.ChatService;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final ChatService chatService;

    @PostMapping("/room/{scheduleId}")
    public ResponseEntity<Boolean> createChatRoom(
            @AuthenticationPrincipal final MemberAdapter memberAdapter,
            @PathVariable final Long scheduleId) {

        return ResponseEntity.ok(
                chatRoomService.createChatRoom(memberAdapter, scheduleId));
    }

    @GetMapping("/room")
    public ResponseEntity<Page<ChatRoomListResponse>> getChatRoomList(
            @AuthenticationPrincipal final MemberAdapter memberAdapter,
            final Pageable pageable) {

        return ResponseEntity.ok(
                chatRoomService.getChatRoomList(memberAdapter, pageable));
    }

    @GetMapping("/room/{chatRoomId}")
    public ResponseEntity<List<ChatResponse>> enterChatRoom(
            @PathVariable final Long chatRoomId,
            @AuthenticationPrincipal final MemberAdapter memberAdapter) {

        return ResponseEntity.ok(
                chatRoomService.enterChatRoom(memberAdapter, chatRoomId));
    }

}
