package DevHeaven.keyword.domain.schedule.repository;

import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.schedule.entity.Schedule;
import DevHeaven.keyword.domain.schedule.entity.ScheduleFriend;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleFriendRepository extends JpaRepository<ScheduleFriend, Long> {
    List<ScheduleFriend> findByMember(Member member);
}
