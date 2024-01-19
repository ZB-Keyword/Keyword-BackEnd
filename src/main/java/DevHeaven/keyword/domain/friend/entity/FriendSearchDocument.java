package DevHeaven.keyword.domain.friend.entity;

import DevHeaven.keyword.domain.friend.type.FriendStatus;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;

@Document(indexName = "friend")
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
//Friend Entity를 사용해서 검색 작업 진행하면 충돌이 발생 -> 별도로 지정해야함.
//'friend'엔티티를 ElasticSearch에서 사용 가능하도록 매핑한 클래스.
public class FriendSearchDocument {

    //FieldType.Text : 단어형태로 검색 -> 전문검색
    //FieldType.Keyword : exact value 즉, 완전 동일한 데이터에 대해 검색
    //FriendDocument라고 작성 했지만, 실제 정보자체는 Member에서 받아오니까
    //Member를 받아야함
    @Id
    private Long id;

    @Id
    private Long memberId;

    @Field(type = FieldType.Text, name = "name")
    private String name;

    @Field(type = FieldType.Text, name = "email")
    private String email;

    @Field(type = FieldType.Keyword, name = "member_id")
    private FriendStatus status;

}
