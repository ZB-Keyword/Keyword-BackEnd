package DevHeaven.keyword.domain.chat.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatResponse {
    private String sender;
    private String imageUrl;
    private String message;
}
