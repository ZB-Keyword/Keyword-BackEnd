package DevHeaven.keyword.domain.friend.entity;

import DevHeaven.keyword.domain.friend.type.FriendStatus;
import DevHeaven.keyword.domain.member.entity.Member;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//Friend Entity를 사용해서 검색 작업 진행하면 충돌이 발생 -> 별도로 지정해야함.
@Document(indexName = "friends")
//'friend'엔티티를 ElasticSearch에서 사용 가능하도록 매핑한 클래스.
public class FriendSearchDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text, name = "status")
    private FriendStatus status;

    @Field(type = FieldType.Text, name = "member_req_id")
    private String memberRequest;

    @Field(type = FieldType.Keyword, name = "member_id")
    private Long friendId;

}
