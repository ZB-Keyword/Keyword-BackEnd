package DevHeaven.keyword.domain.chat.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatDTO {
    private String sender;
    private String profileImageUrl;
    private String message;
}
