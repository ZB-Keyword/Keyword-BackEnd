package DevHeaven.keyword.domain.schedule.repository;

import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.schedule.entity.Schedule;


import DevHeaven.keyword.domain.schedule.type.ScheduleStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    
   @Query(value = "SELECT * FROM schedule s WHERE s.schedule_id in"
            + " (SELECT sf.schedule_id"
            + " FROM schedulefriend sf"
            + " WHERE sf.member_id = :id) "
            + " ORDER BY s.status = 'ONGOING' DESC, s.schedule_at DESC", nativeQuery = true)
    List<Schedule> getScheduleListByMember(@Param(value = "id") Long id, Pageable pageable);

    Optional<Schedule> findByMemberAndScheduleId(Member member, Long scheduleId);

    @Query(value = "SELECT * FROM schedule" +
            " WHERE STATUS = 'ONGOING' and DATEDIFF(schedule_at, NOW()) <= -1", nativeQuery = true)
    List<Schedule> findScheduleEnd();

    @Query(value = "SELECT * FROM schedule" +
            " WHERE STATUS = 'END' and " +
            " DATEDIFF(schedule_at, NOW()) = -3", nativeQuery = true)
    List<Schedule> findScheduleEndThreeDay();

  List<Schedule> findAllByStatusAndScheduleAtBetween(ScheduleStatus ongoing, LocalDateTime now, LocalDateTime localDateTime);

  List<Schedule> findByMember_MemberId(Long memberId);
}
