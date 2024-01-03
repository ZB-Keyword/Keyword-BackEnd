package DevHeaven.keyword.domain.chat.dto;

import DevHeaven.keyword.domain.chat.entity.ChatRoom;
import DevHeaven.keyword.domain.chat.type.ChatRoomStatus;
import DevHeaven.keyword.domain.friend.entity.Friend;
import DevHeaven.keyword.domain.schedule.dto.ScheduleFriendDTO;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {
    private Long chatRoomId;
    private String scheduleTitle;
    private List<ScheduleFriendDTO> friendsName;

    public ChatRoomDTO create(String name) {
        ChatRoomDTO room = new ChatRoomDTO();

        //room.roomId = UUID.randomUUID().toString();
        room.scheduleTitle = name;

        return room;
    }

}
