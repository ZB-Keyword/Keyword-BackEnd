package DevHeaven.keyword.config;

import DevHeaven.keyword.domain.friend.repository.FriendSearchRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackageClasses = FriendSearchRepository.class)
public class ElasticSearchConfig extends ElasticsearchConfiguration {

    //Elasticsearch 서버의 URL(YML)
    @Value("${spring.elastic.url}")
    private String elasticUrl;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticUrl) // Elasticsearch 서버에 연결할 URL
                .build();
    }
}
