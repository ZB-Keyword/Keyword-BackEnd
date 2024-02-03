package DevHeaven.keyword.domain.friend.service;

import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.domain.friend.dto.request.ElasticSearchListRequest;
import DevHeaven.keyword.domain.friend.entity.ElasticSearchDocument;
import DevHeaven.keyword.domain.friend.entity.Friend;
import DevHeaven.keyword.domain.friend.repository.ElasticSearchRepository;
import DevHeaven.keyword.domain.friend.repository.FriendRepository;
import DevHeaven.keyword.domain.friend.type.FriendStatus;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import DevHeaven.keyword.domain.member.type.MemberStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static DevHeaven.keyword.common.exception.type.ErrorCode.MEMBER_NOT_FOUND;
import static DevHeaven.keyword.domain.friend.type.FriendStatus.*;
import static DevHeaven.keyword.domain.member.type.MemberStatus.ACTIVE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticSearchService {

    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;
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

    //검색 결과 반환
    public List<ElasticSearchListRequest> searchMember(final String keyword,
                                                       final MemberAdapter memberAdapter,
                                                       final Pageable pageable) {

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

        List<ElasticSearchListRequest> friendListResponses = searchResults.stream()
                .map(elasticSearchDocument -> {

                    // 나에게 친구 요청한 경우
                    Friend friendRequestToMe = friendRepository.findByMemberRequestMemberIdAndFriendMemberIdAndStatus(
                            elasticSearchDocument.getId(),
                            member.getMemberId(),
                            FRIEND_CHECKING
                    ).orElse(null);

                    // 내가 친구에게 요청한 경우
                    Friend friendRequestFromMe = friendRepository.findByMemberRequestMemberIdAndFriendMemberIdAndStatus(
                            member.getMemberId(),
                            elasticSearchDocument.getId(),
                            FRIEND_CHECKING
                    ).orElse(null);

                    FriendStatus friendStatus;
                    if (friendRequestToMe != null) {

                        friendStatus = getFriendStatusBasedOnState(friendRequestToMe, member.getMemberId());
                    } else if (friendRequestFromMe != null) {

                        friendStatus = getFriendStatusBasedOnState(friendRequestFromMe, member.getMemberId());

                    } else {
                        // FRIEND_ACCEPTED
                        Friend friendAccepted = friendRepository.findByMemberRequestMemberIdAndFriendMemberIdAndStatus(
                                elasticSearchDocument.getId(),
                                member.getMemberId(),
                                FRIEND_ACCEPTED
                        ).orElse(null);

                        // FRIEND_REFUSED
                        Friend friendRefused = friendRepository.findByMemberRequestMemberIdAndFriendMemberIdAndStatus(
                                member.getMemberId(),
                                elasticSearchDocument.getId(),
                                FRIEND_REFUSED
                        ).orElse(null);

                        if (friendAccepted != null) {
                            friendStatus = FriendStatus.FRIEND;
                        } else if (friendRefused != null) {
                            friendStatus = FRIEND_REFUSED;
                        } else {
                            friendStatus = FriendStatus.NOT_FRIEND;
                        }
                    }

                    return mapToFriendSearchListResponse(elasticSearchDocument, friendStatus);


                    Friend friend = (Friend) friendRepository.findByMemberRequestMemberIdAndFriendMemberId(
                            member.getMemberId(),
                            elasticSearchDocument.getId()
                    ).orElse(null);

                    FriendStatus friendStatus;
                    if (friend == null) {
                        friendStatus = FriendStatus.NOT_FRIEND;
                    } else {
                        switch (friend.getStatus()) {
                            case FRIEND_ACCEPTED:
                                friendStatus = FriendStatus.FRIEND;
                                break;
                            case FRIEND_REFUSED:
                                friendStatus = FriendStatus.FRIEND_REFUSED;
                                break;
                            case FRIEND_CHECKING:
                                friendStatus = getFriendStatusBasedOnState(friend, member.getMemberId());
                                break;
                            default:
                                friendStatus = FriendStatus.NOT_FRIEND;
                        }
                    }
                    return mapToFriendSearchListResponse(elasticSearchDocument, friendStatus);

                })
                .collect(Collectors.toList());

        return friendListResponses;
    }

    private FriendStatus getFriendStatusBasedOnState(Friend friend, Long memberId) {

        switch (friend.getStatus()) {
            case FRIEND_ACCEPTED:
                // 이미 서로 친구인 경우
                return FriendStatus.FRIEND;
            case FRIEND_REFUSED:
                // 친구 요청을 거절한 경우
                return FRIEND_REFUSED;
            case FRIEND_CHECKING:
                if (friend.getMemberRequest().getMemberId().equals(memberId)) {
                    // 내가 친구에게 요청한 경우
                    return FriendStatus.FRIEND_REQUEST;
                } else if (friend.getFriend().getMemberId().equals(memberId)) {
                    // 친구가 나에게 요청한 경우
                    return FriendStatus.FRIEND_REQUESTED;
                }
            default:
                // 친구 관계가 아닌 경우
                return FriendStatus.NOT_FRIEND;

        if (friend.getMemberRequest().getMemberId().equals(memberId)) {
            // 내가 친구에게 요청한 경우
            return FriendStatus.FRIEND_REQUEST;
        } else if (friend.getFriend().getMemberId().equals(memberId)) {
            // 친구가 나에게 요청한 경우
            return FriendStatus.FRIEND_REQUESTED;
        } else {
            return FriendStatus.NOT_FRIEND; // 또는 다른 적절한 값을 리턴하거나 예외를 던질 수 있습니다.

        }
    }

    //ElasticSearchDocument 객체를 ElasticSearchListRequest 객체로 매핑하여 반환
    private ElasticSearchListRequest mapToFriendSearchListResponse(final ElasticSearchDocument elasticSearchDocument, FriendStatus friendStatus) {
        return ElasticSearchListRequest.builder()
                .memberId(elasticSearchDocument.getId())
                .name(elasticSearchDocument.getName())
                .email(elasticSearchDocument.getEmail())
                .profileImageFileName(elasticSearchDocument.getProfileImageFileName())
                .status(String.valueOf(elasticSearchDocument.getStatus()))

                .friendStatus(String.valueOf(friendStatus)) //friendstatus 정보는 백에서 처리, ES X


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

