package pl.sknikod.kodemy.infrastructure.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.opensearch.action.admin.indices.alias.Alias;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.update.UpdateRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.CreateIndexResponse;
import org.opensearch.client.indices.GetIndexRequest;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.exception.structure.ServerProcessingException;
import pl.sknikod.kodemy.infrastructure.search.rest.MaterialSearchObject;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final RestHighLevelClient restHighLevelClient;
    private final MaterialSearchMapper materialSearchMapper;
    private final ObjectMapper objectMapper;
    private final RequestOptions requestOptions = RequestOptions.DEFAULT;

    public void indexMaterial(MaterialSearchObject material) {
        createIndexIfNotExists(SearchConfig.MATERIAL_INDEX);
        String indexJSONObject = Try.of(() -> objectMapper.writeValueAsString(material))
                .getOrElseThrow(ex -> new ServerProcessingException(ex.getMessage()));

        IndexRequest request = new IndexRequest(SearchConfig.MATERIAL_INDEX)
                .source(indexJSONObject, XContentType.JSON);
        Try.of(() -> restHighLevelClient.index(request, requestOptions))
                .onFailure(ex -> {
                    throw new ServerProcessingException(ex.getMessage());
                });
    }

    public void reindexMaterial(String documentId, MaterialSearchObject material) {
        String indexJSONObject = Try.of(() -> objectMapper.writeValueAsString(materialSearchMapper.map(material)))
                .getOrElseThrow(ex -> new ServerProcessingException(ex.getMessage()));

        UpdateRequest request = new UpdateRequest(SearchConfig.MATERIAL_INDEX, documentId)
                .doc(indexJSONObject, XContentType.JSON);
        Try.of(() -> restHighLevelClient.update(request, requestOptions))
                .onFailure(ex -> {
                    throw new ServerProcessingException(ex.getMessage());
                });
    }

    public SearchHit[] search(String index, QueryBuilder queryBuilder, Integer limit) {
        SearchRequest searchRequest = createSearchRequest(index, queryBuilder, limit);
        return Try.of(() -> restHighLevelClient.search(searchRequest, requestOptions))
                .onFailure(ex -> {
                    throw new ServerProcessingException(ex.getMessage());
                })
                .map(searchResponse -> searchResponse.getHits().getHits())
                .getOrElseThrow(ex -> new ServerProcessingException(ex.getMessage()));
    }

    private SearchRequest createSearchRequest(String index, QueryBuilder queryBuilder, Integer limit) {
        createIndexIfNotExists(index);
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        if (limit != null) searchSourceBuilder.size(limit);
        searchRequest.source(searchSourceBuilder);
        return searchRequest;
    }

    public void createIndexIfNotExists(String index) {
        indexExists(index).map(isExists -> createIndex(index));
    }

    private Try<Boolean> indexExists(String indexName) {
        return Try.of(() -> restHighLevelClient.indices().exists(new GetIndexRequest(indexName), requestOptions))
                .onFailure(ex -> {
                    throw new ServerProcessingException(ex.getMessage());
                })
                .filter(BooleanUtils::isFalse);
    }

    private Try<CreateIndexResponse> createIndex(String index) {
        CreateIndexRequest request = new CreateIndexRequest(index)
                .alias(new Alias(index + SearchConfig.ALIAS_SUFFIX));
        return Try.of(() -> restHighLevelClient.indices().create(request, requestOptions))
                .onFailure(ex -> {
                    throw new ServerProcessingException(ex.getMessage());
                });
    }
}
