package DevHeaven.keyword.domain.notice.entity;

import DevHeaven.keyword.common.entity.BaseTimeEntity;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.notice.type.NoticeType;
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
public class Notice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @Column(nullable = false)
    private NoticeType type;

    @Column(nullable = false)
    private Long informationId;

    @Builder.Default
    private Boolean isRead = false;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public void modifyNoticeIsRead(){
        if(this.isRead == false){
            this.isRead = true;
        }
    }

    public void setStatus(Boolean isRead) {
        this.isRead = isRead;
    }
}
