package DevHeaven.keyword.domain.chat.repository;

import DevHeaven.keyword.domain.chat.entity.ChatRoom;
import DevHeaven.keyword.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    ChatRoom findBySchedule(Schedule schedule);
}
