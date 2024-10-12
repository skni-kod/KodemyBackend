package pl.sknikod.kodemysearch.util.opensearch;

import io.vavr.control.Try;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.UpdateAliasesRequest;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.endpoints.BooleanResponse;
import org.springframework.util.Assert;

@Slf4j
public class OpenSearchClientEnhanced extends OpenSearchClient {
    public OpenSearchClientEnhanced(OpenSearchTransport transport) {
        super(transport);
    }

    public void initializeIndex(Index index) {
        createIndex(index).flatMap(this::createAlias).getOrElseThrow(th -> new RuntimeException(th));
    }

    private Try<Index> createIndex(Index index) {
        Assert.notNull(index, "indexDetails cannot be null");
        return isIndexExists(index.name)
                .flatMapTry(isExists -> {
                    if (isExists) return Try.success(index);
                    var requestBuilder = new CreateIndexRequest.Builder()
                            .index(index.name).aliases(index.alias, alias -> alias);
                    return Try.of(() -> super.indices().create(builder -> requestBuilder))
                            .onSuccess(response -> log.info("Index was created: {}({})", response.index(), index.alias))
                            .onFailure(th -> log.error("Index cannot be created: {}({})", index.name, index.alias, th))
                            .map(unused -> index);
                });
    }

    private Try<Boolean> isIndexExists(String indexName) {
        Assert.notNull(indexName, "indexName cannot be null");
        return Try.of(() -> super.indices().exists(builder -> builder.index(indexName)))
                .map(BooleanResponse::value)
                .onFailure(th -> log.error("Index could not be checked: {}", indexName, th));
    }

    private Try<Index> createAlias(Index index) {
        Assert.notNull(index, "indexDetails cannot be null");
        return isAliasExists(index.alias, index.name)
                .flatMapTry(isExists -> {
                    if (isExists) return Try.success(index);
                    var requestBuilder = new UpdateAliasesRequest.Builder()
                            .actions(builder -> builder.add(builder1 -> builder1.index(index.name).alias(index.alias)));
                    return Try.of(() -> super.indices().updateAliases(builder -> requestBuilder))
                            .onSuccess(response -> log.info("Alias was created: {}", index.alias))
                            .onFailure(th -> log.error("Alias cannot be created: {}", index.alias, th))
                            .map(unused -> index);
                });
    }

    private Try<Boolean> isAliasExists(String aliasName, String indexName) {
        Assert.notNull(aliasName, "aliasName cannot be null");
        Assert.notNull(indexName, "indexName cannot be null");
        return Try.of(() -> super.indices().existsAlias(builder -> builder.name(aliasName).index(indexName)))
                .map(BooleanResponse::value)
                .onFailure(th -> log.error("Alias could not be checked: {}", aliasName, th));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Index {
        private String name;
        private String alias;
    }
}