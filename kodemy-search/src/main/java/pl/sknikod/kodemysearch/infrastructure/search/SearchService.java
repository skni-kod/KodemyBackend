package pl.sknikod.kodemysearch.infrastructure.search;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.update.UpdateRequest;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemysearch.configuration.OpenSearchConfig;
import pl.sknikod.kodemysearch.exception.structure.ServerProcessingException;
import pl.sknikod.kodemysearch.infrastructure.search.rest.SearchFields;
import pl.sknikod.kodemysearch.infrastructure.search.rest.SingleMaterialResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {
    private final RestHighLevelClient restHighLevelClient;
    private final MaterialSearchMapper materialSearchMapper;
    private final ObjectMapper objectMapper;
    private final OpenSearchConfig.IndexManager indexManager;
    private final OpenSearchConfig.IndexProperties indexProperties;

    public void indexMaterial(QueueConsumer.MaterialEvent material) {
        var index = indexProperties.getIndex();
        indexManager.createIndexIfNotExists(index);
        Try.of(() -> objectMapper.convertValue(material, new TypeReference<Map<String, ?>>() {
                }))
                .map(jsonObject -> new IndexRequest(index.getName())
                        .id(material.getId().toString())
                        .source(jsonObject)
                )
                .onSuccess(request -> Try.of(() -> restHighLevelClient.index(request, OpenSearchConfig.REQUEST_OPTIONS))
                        .onFailure(ex -> log.error(ex.getMessage()))
                );
    }

    public void reindexMaterial(String materialId, QueueConsumer.MaterialEvent material) {
        var index = indexProperties.getIndex();
        indexManager.createIndexIfNotExists(index);

        Try.of(() -> objectMapper.convertValue(material, new TypeReference<Map<String, Object>>() {
                }))
                .onSuccess(jsonObject -> {
                    UpdateRequest request = new UpdateRequest(index.getName(), materialId)
                            .doc(jsonObject)
                            .docAsUpsert(true);
                    Try.of(() -> restHighLevelClient.update(request, OpenSearchConfig.REQUEST_OPTIONS))
                            .onFailure(ex -> log.error(ex.getMessage()));
                })
                .onFailure(ex -> log.error(ex.getMessage()));
    }


    private SearchRequest createMaterialSearchRequest(SearchSourceBuilder sourceBuilder) {
        return new SearchRequest(indexProperties.getIndex().getName())
                .source(sourceBuilder);
    }

    public Page<SingleMaterialResponse> searchMaterials(SearchFields searchFields, Pageable page) {
        SearchSourceBuilder searchSourceBuilder = new SearchBuilder(map(searchFields, page))
                .toSearchSourceBuilder();
        return Try.of(() -> restHighLevelClient.search(
                        createMaterialSearchRequest(searchSourceBuilder), OpenSearchConfig.REQUEST_OPTIONS
                ))
                .onFailure(ex -> {
                    throw new ServerProcessingException(ex.getMessage());
                })
                .map(searchResponse -> {
                    var hits = searchResponse.getHits();
                    return new PageImpl<>(
                            materialSearchMapper.map(hits),
                            page,
                            hits.getTotalHits().value
                    );
                })
                .getOrElseThrow(ex -> new ServerProcessingException(ex.getMessage()));
    }

    private SearchCriteria map(@NonNull SearchFields searchFields, @NonNull Pageable page) {
        List<SearchCriteria.PhraseField> phraseFields = new ArrayList<>();

        if (Objects.nonNull(searchFields.getId()))
            phraseFields.add(new SearchCriteria.PhraseField(
                    "id", searchFields.getId().toString(), false, false
            ));
        if (Objects.nonNull(searchFields.getTitle()))
            phraseFields.add(new SearchCriteria.PhraseField(
                    "title", searchFields.getTitle(), false, false
            ));
        if (Objects.nonNull(searchFields.getStatus()))
            phraseFields.add(new SearchCriteria.PhraseField(
                    "status", searchFields.getStatus(), false, false
            ));
        if (Objects.nonNull(searchFields.getCreatedBy()))
            phraseFields.add(new SearchCriteria.PhraseField(
                    "createdBy", searchFields.getCreatedBy(), false, false
            ));
        if (Objects.nonNull(searchFields.getSectionId()))
            phraseFields.add(new SearchCriteria.PhraseField(
                    "sectionId", searchFields.getSectionId().toString(), false, false
            ));
        if (Objects.nonNull(searchFields.getCategoryId()))
            phraseFields.add(new SearchCriteria.PhraseField(
                    "categoryId", searchFields.getCategoryId().toString(), false, false
            ));
        if (Objects.nonNull(searchFields.getTechnologyIds()) && !searchFields.getTechnologyIds().isEmpty()) {
            phraseFields.add(new SearchCriteria.PhraseField(
                    "technologyIds",
                    StringUtils.join(searchFields.getTechnologyIds(), " "),
                    false,
                    false
            ));
        }

        List<SearchCriteria.RangeField<?>> rangeFields = new ArrayList<>();

        if (Objects.nonNull(searchFields.getCreatedDateFrom()) || Objects.nonNull(searchFields.getCreatedDateTo()))
            rangeFields.add(new SearchCriteria.RangeField<>(
                    "createdDate",
                    searchFields.getCreatedDateFrom(),
                    searchFields.getCreatedDateTo()
            ));

        var contentField = new SearchCriteria.ContentField(searchFields.getPhrase());
        return new SearchCriteria(contentField, phraseFields, rangeFields, page);
    }
}
