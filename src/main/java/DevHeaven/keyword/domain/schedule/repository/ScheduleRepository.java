package DevHeaven.keyword.domain.schedule.repository;

import DevHeaven.keyword.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
