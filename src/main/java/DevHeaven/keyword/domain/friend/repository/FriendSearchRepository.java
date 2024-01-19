package DevHeaven.keyword.domain.friend.repository;


import DevHeaven.keyword.domain.friend.entity.FriendSearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendSearchRepository extends ElasticsearchRepository<FriendSearchDocument, Long> {

    List<FriendSearchDocument> findByNameOrEmail(String name, String email);

}
