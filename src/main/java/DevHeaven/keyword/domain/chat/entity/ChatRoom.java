package DevHeaven.keyword.domain.chat.entity;

import static DevHeaven.keyword.domain.chat.type.ChatRoomStatus.VALID;

import DevHeaven.keyword.common.entity.BaseTimeEntity;
import DevHeaven.keyword.domain.chat.dto.ChatRoomListResponse;
import DevHeaven.keyword.domain.chat.type.ChatRoomStatus;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.schedule.entity.Schedule;
import java.util.stream.Collectors;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private ChatRoomStatus status;

    public static ChatRoom createRoom(Schedule schedule) {
        return ChatRoom.builder()
            .schedule(schedule)
            .member(schedule.getMember())
            .status(VALID)
            .build();
    }

    public static ChatRoomListResponse from(ChatRoom chatRoom) {
        return ChatRoomListResponse.builder()
            .chatRoomId(chatRoom.getId())
            .scheduleTitle(chatRoom.schedule.getTitle())
            .friendsName(
                chatRoom.schedule.getScheduleFriendList()
                    .stream().map(Object::toString)
                    .collect(Collectors.toList()))
            .build();
    }
}
