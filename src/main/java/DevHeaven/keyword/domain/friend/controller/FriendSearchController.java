package DevHeaven.keyword.domain.friend.controller;

import DevHeaven.keyword.domain.friend.entity.FriendSearchDocument;
import DevHeaven.keyword.domain.friend.service.FriendSearchService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/search")
    public List<FriendSearchDocument> searchMembers(@RequestParam String keyword) {
        return friendSearchService.searchMembers(keyword);
    }
}
