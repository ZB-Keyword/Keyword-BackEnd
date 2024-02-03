package DevHeaven.keyword.domain.friend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticSearchListRequest {

    private Long memberId;
    private String imageUrl;
    private String name;
    private String email;
    private String status;
    private String friendStatus;
}
