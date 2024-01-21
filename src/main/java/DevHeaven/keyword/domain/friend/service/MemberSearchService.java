package DevHeaven.keyword.domain.friend.service;

import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.domain.friend.dto.request.MemberSearchListRequest;
import DevHeaven.keyword.domain.friend.entity.MemberSearchDocument;
import DevHeaven.keyword.domain.friend.repository.MemberSearchRepository;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static DevHeaven.keyword.common.exception.type.ErrorCode.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MemberSearchService {

    private final MemberRepository memberRepository;
    private final MemberSearchRepository memberSearchRepository;

    @Transactional
    public void saveAllMember(MemberSearchListRequest memberSearchListRequest) {

        Member member = Member.builder()
                .memberId(memberSearchListRequest.getMemberId())
                .name(memberSearchListRequest.getName())
                .email(memberSearchListRequest.getEmail())
                .build();

        memberRepository.save(member);

        // Elasticsearch에 멤버 정보 저장
        MemberSearchDocument searchDocument = MemberSearchDocument.from(member);
        memberSearchRepository.save(searchDocument);
    }

    @Transactional
    public void saveAllMembersAsDocuments() {
        List<MemberSearchDocument> memberSearchDocumentList = memberRepository.findAll()
                .stream()
                .map(MemberSearchDocument::from)
                .collect(Collectors.toList());
        memberSearchRepository.saveAll(memberSearchDocumentList);
    }

    public List<MemberSearchListRequest> searchMember(String keyword, MemberAdapter memberAdapter, Pageable pageable) {
        // 현재 로그인한 멤버 정보 가져오기
//        log.info("info ={}",memberAdapter.getEmail());
        Member member = memberRepository.findByEmail(memberAdapter.getEmail())
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        // Elasticsearch에서 검색 수행
        Page<MemberSearchDocument> searchResult = memberSearchRepository
                .findAllByNameContainingOrEmailContaining(keyword, keyword, pageable);

        List<MemberSearchListRequest> collect = searchResult.getContent().stream()
                .map(document -> mapToFriendSearchListResponse(document, member))
                .collect(Collectors.toList());
        return collect;
    }

    private MemberSearchListRequest mapToFriendSearchListResponse(MemberSearchDocument memberSearchDocument, Member member) {
        // 'document'와 'member' 정보를 사용하여 응답을 생성
        return MemberSearchListRequest.builder()
                .memberId(memberSearchDocument.getId())
                .name(memberSearchDocument.getName())
                .email(memberSearchDocument.getEmail())
                .profileImageFileName(memberSearchDocument.getProfileImageFileName())
                .build();
    }




}
