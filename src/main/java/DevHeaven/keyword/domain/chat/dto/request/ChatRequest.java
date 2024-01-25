package DevHeaven.keyword.domain.chat.dto.request;

import DevHeaven.keyword.domain.chat.entity.Chat;
import DevHeaven.keyword.domain.chat.entity.ChatRoom;
import DevHeaven.keyword.domain.member.entity.Member;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequest {

    private Long roomId;
    private Long senderId;
    private String sender;
    private String imageUrl;
    private String content;
    private LocalDateTime sendAt;

    public static Chat toEntity(ChatRoom chatRoom, Member sender, String content) {
        return Chat.builder()
            .chatRoom(chatRoom)
            .sender(sender)
            .content(content)
            .build();
    }
}
