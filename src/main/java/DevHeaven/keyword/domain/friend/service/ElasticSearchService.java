package DevHeaven.keyword.domain.friend.service;

import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.domain.friend.dto.request.ElasticSearchListRequest;
import DevHeaven.keyword.domain.friend.entity.ElasticSearchDocument;
import DevHeaven.keyword.domain.friend.repository.ElasticSearchRepository;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import DevHeaven.keyword.domain.member.type.MemberStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static DevHeaven.keyword.common.exception.type.ErrorCode.MEMBER_NOT_FOUND;
import static DevHeaven.keyword.domain.member.type.MemberStatus.ACTIVE;
import static DevHeaven.keyword.domain.member.type.MemberStatus.WITHDRAWN;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticSearchService {

    private final MemberRepository memberRepository;
    private final ElasticSearchRepository elasticSearchRepository;

    //ES에 모든 멤버를 저장
    @Transactional
    public void saveAllMembersAsDocuments() {
        List<Member> memberList = memberRepository.findAllByStatus(ACTIVE);
        if (!memberList.isEmpty()) {
            List<ElasticSearchDocument> elasticSearchDocumentList = memberList.stream()
                    .map(member -> ElasticSearchDocument.from(member, ACTIVE))
                    .collect(Collectors.toList());

            elasticSearchRepository.saveAll(elasticSearchDocumentList);
        }
    }

    public List<ElasticSearchListRequest> searchMember(final String keyword, final MemberAdapter memberAdapter, final Pageable pageable) {

        // 현재 로그인한 멤버 정보 가져오기
        final Member member = memberRepository.findByEmail(memberAdapter.getEmail())
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        // ES에 모든 멤버를 저장
        saveAllMembersAsDocuments();

        // ES 검색 결과 반환
        Page<ElasticSearchDocument> searchResult = elasticSearchRepository
                .findAllByNameContainingOrEmailContaining(keyword, keyword, pageable);

        List<ElasticSearchDocument> searchResults = searchResult.getContent();

//        // WITHDRAWN 상태인 멤버 스케줄러로 특정 시간에 삭제
//        cleanUpWithdrawnMembers();

        return searchResults.stream()
                .map(this::mapToFriendSearchListResponse)
                .collect(Collectors.toList());
    }

    private ElasticSearchListRequest mapToFriendSearchListResponse(final ElasticSearchDocument elasticSearchDocument) {
        return ElasticSearchListRequest.builder()
                .memberId(elasticSearchDocument.getId())
                .name(elasticSearchDocument.getName())
                .email(elasticSearchDocument.getEmail())
                .profileImageFileName(elasticSearchDocument.getProfileImageFileName())
                .build();
    }

    //회원 탈퇴 -> ES에 해당 회원 정보를 저장
    @Transactional
    public void saveWithdrawnMemberAsDocument(final MemberStatus status, final Member member) {

        ElasticSearchDocument withdrawnMemberDocument = ElasticSearchDocument.from(member, status);
        elasticSearchRepository.save(withdrawnMemberDocument);
    }

//    //  회원 탈퇴 -> 스케줄러로 특정 시간에 삭제
//    @Scheduled(cron = "0 0 0 * * *")
//    // (fixedRate = 10000) 테스트용 10초
//    @Transactional
//    public void cleanUpWithdrawnMembers() {
//        elasticSearchRepository.deleteByStatus(WITHDRAWN);
//    }

}
