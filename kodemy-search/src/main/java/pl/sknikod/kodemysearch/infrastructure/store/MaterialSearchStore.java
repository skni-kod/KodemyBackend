package pl.sknikod.kodemysearch.infrastructure.store;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.opensearch.client.opensearch.core.UpdateRequest;
import org.opensearch.client.opensearch.core.UpdateResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemysearch.infrastructure.module.material.SearchCriteria;
import pl.sknikod.kodemysearch.infrastructure.module.material.SearchRequestBuilder;
import pl.sknikod.kodemysearch.infrastructure.module.material.model.MaterialIndexData;
import pl.sknikod.kodemysearch.infrastructure.module.material.model.MaterialStatus;
import pl.sknikod.kodemysearch.util.data.SearchStore;

import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class MaterialSearchStore implements SearchStore<MaterialIndexData> {
    private final String INDEX = "materials";
    private final OpenSearchClient openSearchClient;

    @Override
    public Try<IndexResponse> save(MaterialIndexData material) {
        return Try.of(() -> {
                    var indexRequest = new IndexRequest.Builder<MaterialIndexData>()
                            .index(INDEX).id(material.getId().toString())
                            .document(material).build();
                    return openSearchClient.index(indexRequest);
                })
                .onFailure(th -> log.error("Cannot index material", th));
    }

    @Override
    public Try<UpdateResponse<MaterialIndexData>> update(MaterialIndexData material) {
        return Try.of(() -> {
                    var updateRequest = new UpdateRequest.Builder<MaterialIndexData, MaterialIndexData>()
                            .index(INDEX).id(material.getId().toString())
                            .doc(material).build();
                    return openSearchClient.update(updateRequest, MaterialIndexData.class);
                })
                .onFailure(th -> log.error("Failed to reindex material", th));
    }

    public Try<UpdateResponse<MaterialIndexData>> update(Long materialId, MaterialStatus newStatus) {
        return Try.of(() -> {
                    var updateRequest = new UpdateRequest.Builder<MaterialIndexData, Map<String, String>>()
                            .index(INDEX).id(materialId.toString())
                            .doc(Map.of("status", newStatus.name())).build();
                    return openSearchClient.update(updateRequest, MaterialIndexData.class);
                })
                .onFailure(th -> log.error("Failed to update material status", th));
    }

    public Try<UpdateResponse<MaterialIndexData>> update(Long materialId, Set<MaterialIndexData.Tag> newTags) {
        return Try.of(() -> {
                    var updateRequest = new UpdateRequest.Builder<MaterialIndexData, Map<String, Object>>()
                            .index(INDEX).id(materialId.toString())
                            .doc(Map.of("tags", newTags)).build();
                    return openSearchClient.update(updateRequest, MaterialIndexData.class);
                })
                .onFailure(th -> log.error("Failed to update material tags", th));
    }


    public Try<PageImpl<MaterialIndexData>> search(SearchCriteria criteria) {
        return Try.of(() -> openSearchClient.search(
                        new SearchRequestBuilder(INDEX, criteria).build(), MaterialIndexData.class
                ))
                .mapTry(response -> {
                    final var hits = response.hits();
                    return new PageImpl<MaterialIndexData>(
                            hits.hits().stream().map(Hit::source).toList(),
                            criteria.getPageable(),
                            hits.total().value()
                    );
                })
                .onFailure(th -> log.error("Error during search", th));
    }
}
