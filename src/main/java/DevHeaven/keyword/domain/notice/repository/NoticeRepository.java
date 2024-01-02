package DevHeaven.keyword.domain.notice.repository;

import DevHeaven.keyword.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
