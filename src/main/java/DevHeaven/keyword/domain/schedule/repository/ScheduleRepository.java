package DevHeaven.keyword.domain.schedule.repository;

import DevHeaven.keyword.domain.schedule.entity.Schedule;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query(value = "select * from schedule where schedule_id = ("
        + " select sf.schedule_id"
        + " from schedule_friend sf"
        + " where sf.memberId = :id)", nativeQuery = true)
    List<Schedule> getScheduleList(@Param("id") Long memberId);
}
