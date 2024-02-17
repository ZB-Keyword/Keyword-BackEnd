package DevHeaven.keyword.domain.friend.service;

import DevHeaven.keyword.domain.friend.entity.MemberDocument;
import DevHeaven.keyword.domain.friend.repository.ElasticSearchRepository;
import DevHeaven.keyword.domain.member.entity.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ElasticSearchClient {

  private final ElasticSearchRepository elasticSearchRepository;
  private final ElasticsearchOperations elasticsearchOperations;

  public List <MemberDocument> findAllByNameContainingOrEmailContaining(final String keyword, final
      Pageable pageable){
    return elasticSearchRepository.findAllByNameContainingOrEmailContaining(keyword,keyword,pageable).getContent();
  }

  public void save(final MemberDocument memberDocument){
    elasticSearchRepository.save(memberDocument);
  }

  public void update(final Member member){
    final MemberDocument memberDocument = MemberDocument.from(member);
    final Document document = elasticsearchOperations.getElasticsearchConverter()
        .mapObject(memberDocument);
    final UpdateQuery updateQuery = UpdateQuery.builder(document.getId())
        .withDocument(document)
        .withDocAsUpsert(true)
        .build();
    elasticsearchOperations.update(updateQuery, IndexCoordinates.of("friends"));
  }

  public void delete(final Long id){
    elasticsearchOperations.delete(id, IndexCoordinates.of("friends"));
  }


  //이전에 있던 db 멤버들 저장하기 위한 임시 메서드
  @Transactional
  public void saveAllMembersAsDocuments(final List<MemberDocument> memberDocumentList) {
    if (!memberDocumentList.isEmpty()) {
      elasticSearchRepository.saveAll(memberDocumentList);
    }
  }
}
