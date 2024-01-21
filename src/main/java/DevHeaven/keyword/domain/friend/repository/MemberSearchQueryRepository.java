package DevHeaven.keyword.domain.friend.repository;

import DevHeaven.keyword.domain.friend.dto.request.MemberSearchListRequest;
import DevHeaven.keyword.domain.friend.entity.MemberSearchDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
@Repository
@RequiredArgsConstructor
public class MemberSearchQueryRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    public List<MemberSearchDocument> findByCondition(MemberSearchListRequest memberSearchListRequest, Pageable pageable) {
        CriteriaQuery query = createConditionCriteriaQuery(memberSearchListRequest).setPageable(pageable);

        SearchHits<MemberSearchDocument> search = elasticsearchOperations.search(query, MemberSearchDocument.class);
        return search.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    private CriteriaQuery createConditionCriteriaQuery(MemberSearchListRequest memberSearchListRequest) {
        CriteriaQuery query = new CriteriaQuery(new Criteria());

        if (memberSearchListRequest == null)
            return query;

        if (memberSearchListRequest.getMemberId() != null)
            query.addCriteria(Criteria.where("id").is(memberSearchListRequest.getMemberId()));

        if(StringUtils.hasText(memberSearchListRequest.getName()))
            query.addCriteria(Criteria.where("name").is(memberSearchListRequest.getName()));

        return query;
    }
}

