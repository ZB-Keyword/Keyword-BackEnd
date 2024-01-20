package DevHeaven.keyword.domain.schedule.entity;

import DevHeaven.keyword.common.entity.BaseTimeEntity;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.schedule.dto.ScheduleFriend;
import DevHeaven.keyword.domain.schedule.dto.response.ScheduleDetailResponse;
import DevHeaven.keyword.domain.schedule.dto.response.ScheduleListResponse;
import DevHeaven.keyword.domain.schedule.type.ScheduleStatus;

import java.util.List;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicUpdate
public class Schedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @Column(nullable = false)
    private String title;

    private String contents;

    @Column(nullable = false, updatable = false)
    private LocalDateTime scheduleAt;

    private String locationExplanation;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;

    @Column(nullable = false)
    private Long remindAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @JoinTable(name = "schedulefriend",
            joinColumns = @JoinColumn(name = "schedule_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    @OneToMany
    private List<Member> friendList;

    public void setStatus(ScheduleStatus status) {
        this.status = status;
    }

    public ScheduleListResponse toScheduleList() {
        return ScheduleListResponse
                .builder()
                .scheduleId(this.getScheduleId())
                .title(this.getTitle())
                .scheduleDateTime(this.getScheduleAt())
                .locationExplanation(this.getLocationExplanation())
                .status(this.getStatus())
                .build();
    }

    public ScheduleDetailResponse toScheduleDetail() {
        return ScheduleDetailResponse.builder()
                .organizerId(this.member.getMemberId())
                .title(this.getTitle())
                .contents(this.getContents())
                .scheduleAt(this.getScheduleAt())
                .locationExplanation(this.getLocationExplanation())
                .latitude(this.getLatitude())
                .longitude(this.getLongitude())
                .status(this.getStatus())
                .remindAt(this.getRemindAt())
                .scheduleFriendList(this.toScheduleFriend(this.friendList))
                .build();
    }
    private List<ScheduleFriend> toScheduleFriend(List<Member> memberList) {
        return memberList.stream()
                .map(m -> new ScheduleFriend(m.getMemberId(), m.getName(), m.getProfileImageFileName(), m.getEmail()))
                .collect(Collectors.toList());
    }
}
