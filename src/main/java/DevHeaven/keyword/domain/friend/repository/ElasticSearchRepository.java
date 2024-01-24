package DevHeaven.keyword.domain.friend.repository;

import DevHeaven.keyword.domain.friend.entity.ElasticSearchDocument;
import DevHeaven.keyword.domain.member.type.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticSearchRepository extends ElasticsearchRepository<ElasticSearchDocument, Long> {

    Page<ElasticSearchDocument> findAllByNameContainingOrEmailContaining(String keyword, String keyword1, Pageable pageable);

    void deleteByStatus(MemberStatus memberStatus);


}
