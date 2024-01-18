package DevHeaven.keyword.domain.schedule.entity;

import static DevHeaven.keyword.domain.schedule.type.ScheduleStatus.DELETE;

import DevHeaven.keyword.common.entity.BaseTimeEntity;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.schedule.dto.response.ScheduleListResponse;
import DevHeaven.keyword.domain.schedule.type.ScheduleStatus;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

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

  @Column(nullable = false)
  private LocalDateTime scheduleAt;

  private String locationExplanation;

  @Column(nullable = false)
  private Double latitude;

  @Column(nullable = false)
  private Double longitude;

  @Column(nullable = false)
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

  public ScheduleListResponse from() {
    return ScheduleListResponse
        .builder()
        .scheduleId(this.getScheduleId())
        .title(this.getTitle())
        .scheduleDateTime(this.getScheduleAt())
        .locationExplanation(this.getLocationExplanation())
        .status(this.getStatus())
        .build();
  }

  public void updateSchedule(String title, String contents, LocalDateTime scheduleAt,
      String locationExplanation, Double latitude, Double longitude, Long remindAt) {
    this.title = title;
    this.contents = contents;
    this.scheduleAt = scheduleAt;
    this.locationExplanation = locationExplanation;
    this.latitude = latitude;
    this.longitude = longitude;
    this.remindAt = remindAt;

  }

  public void setScheduleStatus() {
    this.status = DELETE;
  }
}
