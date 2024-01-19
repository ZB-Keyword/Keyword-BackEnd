package DevHeaven.keyword.domain.friend.service;

import DevHeaven.keyword.domain.friend.entity.FriendSearchDocument;
import DevHeaven.keyword.domain.friend.repository.FriendSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendSearchService {

    private final FriendSearchRepository friendSearchRepository;

    public List<FriendSearchDocument> searchFriends(String query) {
        return friendSearchRepository.findByStatus(query);
    }
}
