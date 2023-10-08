package pl.sknikod.kodemy.infrastructure.search;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.exception.structure.ServerProcessingException;
import pl.sknikod.kodemy.infrastructure.common.entity.Material;
import pl.sknikod.kodemy.infrastructure.common.repository.MaterialRepository;
import pl.sknikod.kodemy.infrastructure.search.rest.MaterialSearchObject;
import pl.sknikod.kodemy.infrastructure.search.rest.SearchFields;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {
    private final RestHighLevelClient restHighLevelClient;
    private final MaterialSearchMapper materialSearchMapper;
    private final ObjectMapper objectMapper;
    private final MaterialRepository materialRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final RequestOptions requestOptions = RequestOptions.DEFAULT;
    private final AtomicInteger reindexTasksCounter = new AtomicInteger(0);
    private final AtomicInteger reindexObjectsCounter = new AtomicInteger(0);
    private final AtomicInteger reindexMaterials = new AtomicInteger(0);
    private final Integer MAX_PAGE_SIZE_FOR_INDEX = 2000;
    private final Integer MAX_CONCURRENT_TASKS = 100;

    public void indexMaterial(MaterialSearchObject material) {
        createIndexIfNotExists(SearchConfig.MATERIAL_INDEX);
        String indexJSONObject = Try.of(() -> objectMapper.writeValueAsString(material))
                .getOrElseThrow(ex -> new ServerProcessingException(ex.getMessage()));

        IndexRequest request = new IndexRequest(SearchConfig.MATERIAL_INDEX)
                .id(material.getId().toString())
                .source(indexJSONObject, XContentType.JSON);
        Try.of(() -> restHighLevelClient.index(request, requestOptions))
                .onFailure(ex -> {
                    throw new ServerProcessingException(ex.getMessage());
                });
    }

    public void reindexMaterial(String materialId, MaterialSearchObject material) {
        Map<String, Object> mapObject = objectMapper.convertValue(material, new TypeReference<>() {
        });

        UpdateRequest request = new UpdateRequest(SearchConfig.MATERIAL_INDEX, materialId)
                .doc(mapObject)
                .docAsUpsert(true);

        Try.of(() -> restHighLevelClient.update(request, requestOptions))
                .onFailure(ex -> log.error(ex.getMessage()));
    }

    public ReindexResult reindexMaterialsAsync(Date from, Date to) {
        reindexTasksCounter.set(0);
        reindexObjectsCounter.set(0);
        reindexMaterials.set(0);

        Pageable pageable = PageRequest.of(0, MAX_PAGE_SIZE_FOR_INDEX);
        Page<Material> materialPage;

        do {
            materialPage = materialRepository.findMaterialsInDateRangeWithPage(from, to, pageable);
            var materialsToIndex = materialPage.getContent();

            reindexObjectsCounter.addAndGet(materialsToIndex.size());
            reindexMaterials.addAndGet(materialsToIndex.size());

            executorService.submit(() -> {
                reindexTasksCounter.incrementAndGet();
                materialsToIndex
                        .forEach(material -> {
                            reindexMaterial(material.getId().toString(), materialSearchMapper.map(material));
                            reindexObjectsCounter.decrementAndGet();
                        });
                reindexTasksCounter.decrementAndGet();
            });
            while (reindexTasksCounter.get() > MAX_CONCURRENT_TASKS) ; //wait
            pageable = pageable.next();
        } while (materialPage.hasNext());

        while (reindexObjectsCounter.get() > 0 || reindexTasksCounter.get() > 0) ; // wait
        executorService.shutdown();
        return new ReindexResult(reindexMaterials.get());
    }

    @Value
    public static class ReindexResult {
        long reindexed;
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

    private SearchRequest createSearchRequest(String index, SearchSourceBuilder sourceBuilder) {
        createIndexIfNotExists(index);
        return new SearchRequest(index).source(sourceBuilder);
    }

    public Page<MaterialSearchObject> searchMaterials(SearchFields searchFields, Pageable page) {
        SearchSourceBuilder searchSourceBuilder = new SearchBuilder(mapCriteria(searchFields, page))
                .toSearchSourceBuilder();
        return Try.of(() -> restHighLevelClient.search(
                        createSearchRequest(SearchConfig.MATERIAL_INDEX, searchSourceBuilder), requestOptions
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

    private SearchCriteria mapCriteria(SearchFields searchFields, Pageable page) {
        List<SearchCriteria.PhraseField> phraseFields = new ArrayList<>();
        List<SearchCriteria.RangeField<?>> rangeFields = new ArrayList<>();
        var contentField = new SearchCriteria.ContentField(searchFields.getPhrase());

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
        if (Objects.nonNull(searchFields.getCreatedDateFrom()) || Objects.nonNull(searchFields.getCreatedDateTo()))
            rangeFields.add(new SearchCriteria.RangeField<>(
                    "createdDate",
                    searchFields.getCreatedDateFrom(),
                    searchFields.getCreatedDateTo()
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

        return new SearchCriteria(contentField, phraseFields, rangeFields, page);
    }
}
