package DevHeaven.keyword.domain.friend.repository;

import DevHeaven.keyword.domain.friend.entity.MemberSearchDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberSearchRepository extends ElasticsearchRepository<MemberSearchDocument, Long> {

    Page<MemberSearchDocument> findAllByNameContainingOrEmailContaining(String keyword, String keyword1, Pageable pageable);


}
