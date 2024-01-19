package DevHeaven.keyword.domain.friend.service;

import DevHeaven.keyword.domain.friend.entity.Friend;
import DevHeaven.keyword.domain.friend.entity.FriendSearchDocument;
import DevHeaven.keyword.domain.friend.repository.FriendRepository;
import DevHeaven.keyword.domain.friend.repository.FriendSearchRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendSearchService {

    private final FriendSearchRepository friendSearchRepository;

    private final ElasticsearchOperations elasticsearchOperations;

    private final FriendRepository friendRepository;

    //이름, 이메일로 검색
    public List<FriendSearchDocument> searchMembers(String keyword) {
        return friendSearchRepository.findByNameOrEmail(keyword, keyword);
    }

    //MariaDB -> Elasticsearch로 데이터 가져와서 색인
    public void indexFriendsToElasticsearch() {
        List<Friend> friends = friendRepository.findAll();

        List<FriendSearchDocument> friendSearchDocuments = friends.stream()
                .map(this::convertToFriendSearchDocument)
                .collect(Collectors.toList());

        IndexCoordinates indexCoordinates = IndexCoordinates.of("friend");
        elasticsearchOperations.indexOps(indexCoordinates)
                .delete();
        elasticsearchOperations.indexOps(indexCoordinates)
                .refresh();
        elasticsearchOperations.save(friendSearchDocuments);
    }

    private FriendSearchDocument convertToFriendSearchDocument(Friend friend) {
        return FriendSearchDocument.builder()
                .id(friend.getId())
                .memberId(friend.getFriend().getMemberId())
                .name(friend.getFriend().getName())
                .email(friend.getFriend().getEmail())
                .status(friend.getStatus())
                .build();
    }
}
