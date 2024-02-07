package DevHeaven.keyword.domain.friend.repository;

import DevHeaven.keyword.domain.friend.entity.MemberDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticSearchRepository extends ElasticsearchRepository<MemberDocument, Long> {
    Page <MemberDocument> findAllByNameContainingOrEmailContaining(String keyword ,
        String keyword1 , Pageable pageable);
}
