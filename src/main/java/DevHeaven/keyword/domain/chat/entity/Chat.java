package DevHeaven.keyword.domain.chat.entity;

import DevHeaven.keyword.common.entity.BaseTimeEntity;
import DevHeaven.keyword.domain.chat.dto.ChatDTO;
import DevHeaven.keyword.domain.member.entity.Member;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Chat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member sender;

    @Column(nullable = false)
    private String content;

    public ChatDTO from() {
        return ChatDTO.builder()
            .sender(this.sender.getName())
            .profileImageUrl(this.sender.getImageUrl())
            .message(content)
            .build();
    }

    public static Chat createChat(ChatRoom chatRoom, Member sender, String content) {
        return Chat.builder()
            .chatRoom(chatRoom)
            .sender(sender)
            .content(content)
            .build();
    }
}
