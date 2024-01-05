package DevHeaven.keyword.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StompChatController {

    //특정 broker로 메세지 전달
    private final SimpMessagingTemplate template;

    //client가 SEND 할 수 있는 경로
    //StompWebSocketConfig에서 설정한 applicationDestinationPrefixes와 @MessageMapping 경로가 병합됨
    // "/pub/chats/message"
    @MessageMapping(value = "chats/message")
    public void message() {
        template.convertAndSend("/sub/chat/");
    }
}
