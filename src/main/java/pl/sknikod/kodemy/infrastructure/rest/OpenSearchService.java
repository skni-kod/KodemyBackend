package pl.sknikod.kodemy.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.opensearch.action.admin.indices.alias.Alias;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.action.search.SearchRequest;
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
import pl.sknikod.kodemy.infrastructure.model.entity.Material;
import pl.sknikod.kodemy.infrastructure.rest.mapper.MaterialOpenSearchMapper;

@Service
@RequiredArgsConstructor
public class OpenSearchService {
    private final RestHighLevelClient restHighLevelClient;
    private final RequestOptions requestOptions;
    private final MaterialOpenSearchMapper materialOpenSearchMapper;
    private final ObjectMapper objectMapper;

    public Try<IndexResponse> indexMaterial(Material material) {
        String indexJSONObject = Try.of(() -> objectMapper.writeValueAsString(materialOpenSearchMapper.map(material)))
                .getOrElseThrow(ex -> new ServerProcessingException(ex.getMessage()));

        IndexRequest request = new IndexRequest(Info.MATERIAL.index)
                .source(indexJSONObject, XContentType.JSON);
        return Try.of(() -> restHighLevelClient.index(request, requestOptions))
                .onFailure(ex -> {
                    throw new ServerProcessingException();
                });
    }

    public SearchHit[] search(Info.Details details, QueryBuilder queryBuilder, Integer limit) {
        SearchRequest searchRequest = createSearchRequest(details, queryBuilder, limit);
        return Try.of(() -> restHighLevelClient.search(searchRequest, requestOptions))
                .onFailure(ex -> {
                    throw new ServerProcessingException(ex.getMessage());
                })
                .map(searchResponse -> searchResponse.getHits().getHits())
                .getOrElseThrow(ex -> new ServerProcessingException(ex.getMessage()));
    }

    private SearchRequest createSearchRequest(Info.Details details, QueryBuilder queryBuilder, Integer limit) {
        createIndexIfNotExists(details);
        SearchRequest searchRequest = new SearchRequest(details.index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        if (limit != null) searchSourceBuilder.size(limit);
        searchRequest.source(searchSourceBuilder);
        return searchRequest;
    }

    public void createIndexIfNotExists(Info.Details details) {
        indexExists(details.index).map(isExists -> createIndex(details));
    }

    private Try<Boolean> indexExists(String indexName) {
        return Try.of(() -> restHighLevelClient.indices().exists(new GetIndexRequest(indexName), requestOptions))
                .onFailure(ex -> {
                    throw new ServerProcessingException(ex.getMessage());
                })
                .filter(BooleanUtils::isFalse);
    }

    private Try<CreateIndexResponse> createIndex(Info.Details details) {
        CreateIndexRequest request = new CreateIndexRequest(details.index)
                .alias(new Alias(details.alias));
        return Try.of(() -> restHighLevelClient.indices().create(request, requestOptions))
                .onFailure(ex -> {
                    throw new ServerProcessingException(ex.getMessage());
                });
    }

    public static class Info {
        public static final Details MATERIAL = new Details("materials", "materials-alias");

        record Details(String index, String alias) {
        }
    }

}
