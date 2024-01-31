package DevHeaven.keyword.domain.friend.entity;

import DevHeaven.keyword.domain.friend.type.FriendStatus;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.type.MemberStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;


@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document(indexName = "friends")
public class ElasticSearchDocument {

    //FieldType.Text : 단어형태로 검색 -> 전문검색
    //FieldType.Keyword : exact value 즉, 완전 동일한 데이터에 대해 검색
    @Id
    @Field(type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.Text)
    private String profileImageFileName;

    @Field(type = FieldType.Text, name = "name")
    private String name;

    @Field(type = FieldType.Text, name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Enumerated(EnumType.STRING)
    private FriendStatus friendStatus = FriendStatus.FRIEND_UNKNOWN;;

    public void ElasticModifyFriendStatus(FriendStatus friendStatus) {
        this.friendStatus = friendStatus;
    }

    public static ElasticSearchDocument from(final Member member,
                                             final MemberStatus status,
                                             final FriendStatus friendStatus) {

        // S3 이미지 URL 생성
        String s3ImageUrl = "https://d19jzgrojjrozm.cloudfront.net/" + member.getProfileImageFileName();

        return ElasticSearchDocument.builder()
                .id(member.getMemberId())
                .profileImageFileName(s3ImageUrl)
                .name(member.getName())
                .email(member.getEmail())
                .status(status)
                .friendStatus(friendStatus)
                .build();
    }


}
