package pl.sknikod.kodemysearch.infrastructure.material;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemysearch.configuration.OpenSearchConfig;
import pl.sknikod.kodemysearch.exception.structure.ServerProcessingException;
import pl.sknikod.kodemysearch.exception.structure.ValidationException;
import pl.sknikod.kodemysearch.infrastructure.search.SearchBuilder;
import pl.sknikod.kodemysearch.infrastructure.search.SearchCriteria;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaterialGetUseCase {
    private final RestHighLevelClient restHighLevelClient;
    private final MaterialSearchMapper materialSearchMapper;
    private final OpenSearchConfig.IndexProperties indexProperties;

    public Page<MaterialPageable> searchMaterials(SearchFields searchFields, Pageable page) {
        var searchRequest = new SearchRequest(indexProperties.getIndex().getName())
                .source(new SearchBuilder(map(searchFields, page)).toSearchSourceBuilder());
        return Try.of(() -> restHighLevelClient.search(
                        searchRequest, OpenSearchConfig.REQUEST_OPTIONS
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

    private SearchCriteria map(@NonNull MaterialGetUseCase.SearchFields searchFields, @NonNull Pageable page) {
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
        var categoryIds = searchFields.getCategoryIds();
        if (Objects.nonNull(categoryIds) && categoryIds.length != 0) {
            phraseFields.add(new SearchCriteria.PhraseField(
                    "categoryIds",
                    StringUtils.join(categoryIds, ' '),
                    false,
                    false
            ));
        }
        var technologyIds = searchFields.getTechnologyIds();
        if (Objects.nonNull(technologyIds) && technologyIds.length != 0) {
            phraseFields.add(new SearchCriteria.PhraseField(
                    "technologyIds",
                    StringUtils.join(searchFields.getTechnologyIds(), ' '),
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

    @Mapper(componentModel = "spring")
    public interface MaterialSearchMapper {
        ObjectMapper objectMapper = new ObjectMapper();

        default List<MaterialPageable> map(SearchHits hits) {
            return StreamSupport.stream(hits.spliterator(), false)
                    .map(SearchHit::getSourceAsString)
                    .map(this::map)
                    .toList();
        }

        private MaterialPageable map(String source) {
            return Try.of(() -> objectMapper.readValue(source, MaterialPageable.class))
                    .onFailure(ex -> {
                        throw new ServerProcessingException(ex.getMessage());
                    })
                    .get();
        }
    }

    @Data
    @NoArgsConstructor
    public static class SearchFields {
        String phrase;
        Long id;
        String title;
        String status;
        String createdBy;
        Date createdDateFrom;
        Date createdDateTo;
        Long sectionId;
        Long[] categoryIds;
        Long[] technologyIds;

        @Component
        @RequiredArgsConstructor
        public static class MaterialSearchFieldsConverter implements Converter<String, SearchFields> {
            private final ObjectMapper objectMapper;

            @Override
            public SearchFields convert(@NonNull String source) {
                return Try.of(() -> objectMapper.readValue(source, SearchFields.class))
                        .getOrElseThrow(() -> new ValidationException("Can't parse " + getClass().getSimpleName() + " params: " + source));
            }
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MaterialPageable {
        private Long id;
        private String title;
        private String description;
        private MaterialStatus status;
        private boolean isActive;
        private double avgGrade;
        private AuthorDetails author;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private Date createdDate;
        private Long sectionId;
        private Long categoryId;
        private List<TechnologyDetails> technologies;

        public enum MaterialStatus {
            APPROVED, //CONFIRMED
            PENDING, //UNCONFIRMED, AWAITING_APPROVAL, PENDING
            REJECTED,
            EDITED, //CORRECTED
            BANNED
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TechnologyDetails {
            private Long id;
            private String name;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class AuthorDetails {
            private Long id;
            private String username;
        }
    }
}
