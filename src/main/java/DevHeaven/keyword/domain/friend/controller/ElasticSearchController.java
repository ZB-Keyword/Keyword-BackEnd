package DevHeaven.keyword.domain.friend.controller;

import DevHeaven.keyword.domain.friend.dto.request.ElasticSearchListRequest;
import DevHeaven.keyword.domain.friend.service.ElasticSearchService;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class ElasticSearchController {

    private final ElasticSearchService elasticSearchService;

    @GetMapping("/search")
    public ResponseEntity<List<ElasticSearchListRequest>> searchFriends(
            @RequestParam String keyword,
            @AuthenticationPrincipal MemberAdapter memberAdapter,
            Pageable pageable) {

        List<ElasticSearchListRequest> memberlist = elasticSearchService.searchMember(keyword, memberAdapter, pageable);

        return ResponseEntity.ok(memberlist);
    }

}
