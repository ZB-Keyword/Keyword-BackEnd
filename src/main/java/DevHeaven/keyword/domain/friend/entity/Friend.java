package DevHeaven.keyword.domain.friend.entity;

import DevHeaven.keyword.common.entity.BaseTimeEntity;
import DevHeaven.keyword.domain.friend.type.FriendStatus;
import DevHeaven.keyword.domain.member.entity.Member;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Friend extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FriendStatus status;

    @ManyToOne
    @JoinColumn(name = "member_req_id") // 변경된 부분
    private Member memberRequest;

    @ManyToOne
    @JoinColumn(name = "member_id") // 변경된 부분
    private Member friend;

    public void modifyFriendStatus(final FriendStatus friendStatus){
        this.status=friendStatus;
    }



//    (fetch = FetchType.LAZY)
}
