package DevHeaven.keyword.domain.schedule.repository;

import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.schedule.entity.Schedule;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query(value = "SELECT * FROM schedule s WHERE s.schedule_id in"
            + " (SELECT sf.schedule_id"
            + " FROM schedulefriend sf"
            + " WHERE sf.member_id = :id)"
            + " ORDER BY s.status = 'ONGOING' DESC, s.schedule_at DESC;", nativeQuery = true)
    List<Schedule> getScheduleListByMember(Long id);

    Optional<Schedule> findByMemberAndScheduleId(Member member, Long scheduleId);

    @Query(value = "SELECT * FROM schedule" +
            " WHERE STATUS = 'ONGOING' and abs(DATEDIFF(schedule_at, NOW())) >= 1", nativeQuery = true)
    List<Schedule> findScheduleEnd();

    @Query(value = "SELECT * FROM schedule" +
            " WHERE ABS(timestampdiff(DAY, updated_at, NOW())) <= 1 and" +
            " STATUS = 'END' and abs(DATEDIFF(schedule_at, NOW())) >= 3", nativeQuery = true)
    List<Schedule> findScheduleEndThreeDay();
}
