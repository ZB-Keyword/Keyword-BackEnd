package DevHeaven.keyword.domain.friend.controller;

import DevHeaven.keyword.domain.friend.entity.FriendSearchDocument;
import DevHeaven.keyword.domain.friend.service.FriendSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendSearchController {

    private final FriendSearchService friendSearchService;

    @GetMapping("/search-friends")
    public List<FriendSearchDocument> searchFriends(@RequestParam String query) {
        // 검색어를 받아 FriendSearchService를 이용하여 Elasticsearch에서 검색 수행
        return friendSearchService.searchFriends(query);
    }
}
