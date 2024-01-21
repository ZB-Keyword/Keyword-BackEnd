package DevHeaven.keyword.domain.friend.entity;

import DevHeaven.keyword.domain.member.entity.Member;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;


@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document(indexName = "friend")
public class MemberSearchDocument {

    //FieldType.Text : 단어형태로 검색 -> 전문검색
    //FieldType.Keyword : exact value 즉, 완전 동일한 데이터에 대해 검색
    @Id
    @Field(type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.Text, name = "profileImageFileName")
    private String profileImageFileName;

    @Field(type = FieldType.Text, name = "name")
    private String name;

    @Field(type = FieldType.Text, name = "email")
    private String email;

    public static MemberSearchDocument from(Member member) {
        return MemberSearchDocument.builder()
                .id(member.getMemberId())
                .profileImageFileName(member.getProfileImageFileName())
                .name(member.getName())
                .email(member.getEmail())
                .build();
    }

}
