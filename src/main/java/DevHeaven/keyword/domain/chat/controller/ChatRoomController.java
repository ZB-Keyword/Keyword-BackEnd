package DevHeaven.keyword.domain.chat.controller;

import DevHeaven.keyword.domain.chat.dto.ChatRoomDTO;
import DevHeaven.keyword.domain.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
@Slf4j
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    /**
     * 자신의 채팅방 목록 조회
     */
    @GetMapping
    public Page<ChatRoomDTO> getChatRoom(Pageable pageable) {
        //파라미터로 UserAdapter 추가해야함
        //서비스 단에 넘겨줘야함
        return chatRoomService.getChatRoom(pageable);
    }
}
