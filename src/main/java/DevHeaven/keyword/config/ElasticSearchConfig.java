package DevHeaven.keyword.config;

import DevHeaven.keyword.domain.friend.repository.ElasticSearchRepository;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackageClasses = ElasticSearchRepository.class)
public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {

    @Value("${spring.elasticsearch.url}")
    private String elasticUrl;
    @Override
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(elasticUrl)
                .build();
        return RestClients.create(clientConfiguration).rest();
    }
}
