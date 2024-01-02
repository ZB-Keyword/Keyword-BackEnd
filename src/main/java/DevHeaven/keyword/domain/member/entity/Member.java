package DevHeaven.keyword.domain.member.entity;

import DevHeaven.keyword.common.entity.BaseTimeEntity;
import DevHeaven.keyword.domain.friend.entity.Friend;
import DevHeaven.keyword.domain.member.type.MemberStatus;
import DevHeaven.keyword.domain.notice.entity.Notice;
import DevHeaven.keyword.domain.schedule.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private MemberStatus status;

    // TODO : OneToMany 알아보고 삭제 결정
    @OneToMany
    private List<Notice> noticeList;

    @OneToMany
    private List<Schedule> scheduleList;
    
    @OneToMany
    private List<Friend> friendList;
}
