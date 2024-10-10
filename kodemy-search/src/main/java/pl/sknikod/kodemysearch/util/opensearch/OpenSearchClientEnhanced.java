package pl.sknikod.kodemysearch.util.opensearch;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.indices.*;
import org.opensearch.client.transport.endpoints.BooleanResponse;
import org.springframework.util.Assert;
import pl.sknikod.kodemysearch.configuration.OpenSearchConfig;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class OpenSearchClientEnhanced {
    private final OpenSearchClient openSearchClient;

    public static OpenSearchClientEnhanced of(OpenSearchClient client) {
        return new OpenSearchClientEnhanced(client);
    }

    public void initialize(Map<String, OpenSearchConfig.OpenSearchProperties.IndexDetails> indices) {
        extractIndices(indices).forEach(this::createIndexIfNotExists);
    }

    private List<OpenSearchConfig.OpenSearchProperties.IndexDetails> extractIndices(
            Map<String, OpenSearchConfig.OpenSearchProperties.IndexDetails> indices) {
        return indices.values().stream().toList();
    }

    private void createIndexIfNotExists(OpenSearchConfig.OpenSearchProperties.IndexDetails index) {
        var isExists = checkExists(index.getName())
                .getOrElseThrow(th -> new RuntimeException(th));
        if (isExists) return;
        log.warn("Index does not exist: {}", index.getName());
        this.createIndex(index).getOrElseThrow(th -> new RuntimeException(th));
    }

    private Try<Boolean> checkExists(String indexName) {
        Assert.notNull(indexName, "indexName cannot be null");
        return Try.of(() -> openSearchClient.indices().exists(builder -> builder.index(indexName)))
                .onFailure(th -> log.error("Index could not be checked: {}", indexName, th))
                .map(BooleanResponse::value);
    }

    private Try<CreateIndexResponse> createIndex(OpenSearchConfig.OpenSearchProperties.IndexDetails index) {
        final var createIndexRequest = new CreateIndexRequest.Builder()
                .index(index.getName()).build();
        final var indexSettings = new IndexSettings.Builder().autoExpandReplicas("0-all").build();
        final var putIndicesSettingsRequest = new PutIndicesSettingsRequest.Builder()
                .index(index.getName()).settings(indexSettings).build();
        final var putAliasRequest = new PutAliasRequest.Builder()
                .index(index.getName()).name(index.getAlias()).build();
        return Try.of(() -> {
                    var response = openSearchClient.indices().create(createIndexRequest);
                    openSearchClient.indices().putSettings(putIndicesSettingsRequest);
                    openSearchClient.indices().putAlias(putAliasRequest);
                    return response;
                })
                .onSuccess(response -> log.info("Index was created: {}", response.index()))
                .onFailure(th -> log.error("Index cannot be created: {}", index.getName(), th));
    }
}