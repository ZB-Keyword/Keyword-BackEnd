package DevHeaven.keyword.domain.friend.controller;

import DevHeaven.keyword.domain.friend.dto.request.MemberSearchListRequest;
import DevHeaven.keyword.domain.friend.service.MemberSearchService;
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
public class MemberSearchController {

    private final MemberSearchService memberSearchService;

    @GetMapping("/search")
    public ResponseEntity<List<MemberSearchListRequest>> searchFriends(
            @RequestParam String keyword,
            @AuthenticationPrincipal MemberAdapter memberAdapter,
            Pageable pageable) {

        List<MemberSearchListRequest> memberlist = memberSearchService.searchMember(keyword, memberAdapter, pageable);

        return ResponseEntity.ok(memberlist);
    }
}
