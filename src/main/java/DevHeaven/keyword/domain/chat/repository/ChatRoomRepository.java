package DevHeaven.keyword.domain.chat.repository;

import DevHeaven.keyword.domain.chat.entity.ChatRoom;
import DevHeaven.keyword.domain.chat.type.ChatRoomStatus;
import DevHeaven.keyword.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    ChatRoom findBySchedule(Schedule schedule);

    Optional<ChatRoom> findByScheduleAndStatus(Schedule schedule, ChatRoomStatus chatRoomStatus);
}
