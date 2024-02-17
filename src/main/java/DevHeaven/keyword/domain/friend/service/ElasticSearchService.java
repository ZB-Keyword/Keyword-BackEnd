package DevHeaven.keyword.domain.friend.service;

import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.common.service.image.AmazonS3FileService;
import DevHeaven.keyword.domain.friend.dto.response.FriendSearchListResponse;
import DevHeaven.keyword.domain.friend.entity.MemberDocument;
import DevHeaven.keyword.domain.friend.repository.FriendRepository;
import DevHeaven.keyword.domain.friend.type.FriendResponseStatus;
import DevHeaven.keyword.domain.friend.type.FriendStatus;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static DevHeaven.keyword.common.exception.type.ErrorCode.MEMBER_NOT_FOUND;
import static DevHeaven.keyword.domain.friend.type.FriendResponseStatus.*;
import static DevHeaven.keyword.domain.friend.type.FriendResponseStatus.FRIEND;
import static DevHeaven.keyword.domain.friend.type.FriendResponseStatus.FRIEND_REQUEST;
import static DevHeaven.keyword.domain.friend.type.FriendResponseStatus.FRIEND_REQUESTED;
import static DevHeaven.keyword.domain.friend.type.FriendResponseStatus.NOT_FRIEND;
import static DevHeaven.keyword.domain.friend.type.FriendStatus.*;
import static DevHeaven.keyword.domain.member.type.MemberStatus.ACTIVE;


@Service
@RequiredArgsConstructor
public class ElasticSearchService {

    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;
    private final AmazonS3FileService fileService;
    private final ElasticSearchClient elasticSearchClient;

    public void save(final Member member){
        elasticSearchClient.save(MemberDocument.from(member));
    }

    //검색 결과 반환
    public List<FriendSearchListResponse> searchMember(final String keyword,
                                                       final MemberAdapter memberAdapter,
                                                       final Pageable pageable) {

        final Member member = memberRepository.findByEmail(memberAdapter.getEmail())
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        final List<MemberDocument> searchResult = elasticSearchClient
                .findAllByNameContainingOrEmailContaining(keyword, pageable);
        return searchResult.stream()
                .map(memberDocument -> createFriendSearchListResponse(memberDocument,member))
                .collect(Collectors.toList());
    }

    private FriendSearchListResponse createFriendSearchListResponse(final MemberDocument memberDocument, final Member member) {
        //내가 나를 검색하면 안되니까
        if (member.getMemberId() != memberDocument.getId()) {
            if (checkFriend(memberDocument.getId(), member.getMemberId(), FRIEND_CHECKING)) {
                return buildFriendSearchListResponse(memberDocument, FRIEND_REQUESTED);
            } else if (checkFriend(member.getMemberId(), memberDocument.getId(), FRIEND_CHECKING)) {
                return buildFriendSearchListResponse(memberDocument, FRIEND_REQUEST);
            } else if (checkFriend(member.getMemberId(), memberDocument.getId(), FRIEND_ACCEPTED)) {
                return buildFriendSearchListResponse(memberDocument, FRIEND);
            } else {
                return buildFriendSearchListResponse(memberDocument, NOT_FRIEND);
            }
        } else {
            return buildFriendSearchListResponse(memberDocument, ME);
        }
    }

    private boolean checkFriend(final Long friendRequestId ,final Long friendRequestedId,final FriendStatus status) {
        return friendRepository.findByMemberRequestMemberIdAndFriendMemberIdAndStatus(
            friendRequestId , friendRequestedId , status
        ).isPresent();
    }

    private FriendSearchListResponse buildFriendSearchListResponse(final MemberDocument memberDocument,final FriendResponseStatus status) {
        return FriendSearchListResponse.builder()
            .memberId(memberDocument.getId())
            .name(memberDocument.getName())
            .email(memberDocument.getEmail())
            .imageUrl(fileService.createUrl(memberDocument.getProfileImageFileName()))
            .status(status)
            .build();
    }

    public void update(final Member member){
        elasticSearchClient.update(member);
    }

    public void delete(final Long id){
        elasticSearchClient.delete(id);
    }


    //이미 db에 있는 회원들 elastic에 저장하기 위한 임시 메서드
    @Transactional
    public void saveAllMembersAsDocuments() {
        List<Member> memberList = memberRepository.findAllByStatus(ACTIVE);
        if (!memberList.isEmpty()) {
            List<MemberDocument> memberDocumentList = memberList.stream()
                .map(member -> MemberDocument.from(member))
                .collect(Collectors.toList());
            elasticSearchClient.saveAllMembersAsDocuments(memberDocumentList);
        }
    }

}

