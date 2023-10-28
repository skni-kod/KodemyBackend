package pl.sknikod.kodemysearch.configuration;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.http.HttpHost;
import org.opensearch.action.admin.indices.alias.Alias;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.CreateIndexResponse;
import org.opensearch.client.indices.GetIndexRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemysearch.exception.structure.ServerProcessingException;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class OpenSearchConfig {
    public static final RequestOptions REQUEST_OPTIONS = RequestOptions.DEFAULT;
    @Value("${kodemy.opensearch.host}")
    private String host;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        HttpHost httpHost = Optional.of(host)
                .map(HttpHost::create)
                .map(HttpHost::new)
                .get();
        return new RestHighLevelClient(RestClient.builder(httpHost));
    }

    @Data
    @Component
    @ConfigurationProperties("kodemy.opensearch")
    @DependsOn("openSearchConfig")
    public static class IndexProperties {
        private IndexDetails index;

        @Data
        public static class IndexDetails {
            private String name;
            private String alias;
        }
    }

    @Component
    @DependsOn("openSearchConfig")
    public static class IndexManager {
        private final RestHighLevelClient restHighLevelClient;

        protected IndexManager(RestHighLevelClient restHighLevelClient, IndexProperties indexProperties) {
            this.restHighLevelClient = restHighLevelClient;
            createIndexIfNotExists(indexProperties.getIndex());
        }

        public void createIndexIfNotExists(OpenSearchConfig.IndexProperties.IndexDetails index) {
            indexExists(index.getName()).filter(isExists -> !isExists).map(isExists -> createIndex(index));
        }

        private Try<Boolean> indexExists(String indexName) {
            return Try.of(() -> restHighLevelClient.indices().exists(new GetIndexRequest(indexName), REQUEST_OPTIONS))
                    .onFailure(ex -> {
                        throw new ServerProcessingException(ex.getMessage());
                    })
                    .filter(BooleanUtils::isFalse);
        }

        private Try<CreateIndexResponse> createIndex(OpenSearchConfig.IndexProperties.IndexDetails index) {
            CreateIndexRequest request = new CreateIndexRequest(index.getName())
                    .alias(new Alias(index.getAlias()));

            return Try.of(() -> restHighLevelClient.indices().create(request, REQUEST_OPTIONS))
                    .onFailure(ex -> {
                        throw new ServerProcessingException(ex.getMessage());
                    });
        }
    }
}
