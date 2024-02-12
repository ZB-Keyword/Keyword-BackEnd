package DevHeaven.keyword.domain.notice.repository;

import DevHeaven.keyword.domain.notice.entity.Notice;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

  Page<Notice> findByIdAndIsRead(Long memberId, boolean b, Pageable pageable);
}
