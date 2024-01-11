package DevHeaven.keyword.domain.chat.dto.response;

import java.util.List;
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
public class ChatRoomListResponse {
    private Long chatRoomId;
    private String scheduleTitle;
    private List<String> friendsName;

    public ChatRoomListResponse create(String name) {
        ChatRoomListResponse room = new ChatRoomListResponse();

        //room.roomId = UUID.randomUUID().toString();
        room.scheduleTitle = name;

        return room;
    }

}
