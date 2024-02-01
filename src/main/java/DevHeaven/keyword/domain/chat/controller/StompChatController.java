package DevHeaven.keyword.domain.chat.controller;

import DevHeaven.keyword.domain.chat.dto.request.ChatRequest;
import DevHeaven.keyword.domain.chat.entity.Chat;
import DevHeaven.keyword.domain.chat.service.ChatService;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StompChatController {

    //특정 broker로 메세지 전달
    private final SimpMessagingTemplate template;
    private final ChatService chatService;
    private final MemberRepository memberRepository;

    //client가 SEND 할 수 있는 경로
    //StompWebSocketConfig에서 설정한 applicationDestinationPrefixes와 @MessageMapping 경로가 병합됨
    // "/pub/chats/message"
    @MessageMapping(value = "chats/message")
    public void message(ChatRequest message) {

        //db에 메세지 저장
        chatService.createChat(message);

        //회원 조회후 프로필 사진 재설정해서 전송
        Member member = memberRepository.findById(message.getSenderId()).get();
        message.setImageUrl(member.getProfileImageFileName());
        
        //구독하고 있는 클라이언트에 메세지 전송
        template.convertAndSend("/sub/chats/room/" + message.getRoomId(), message);

    }
}
