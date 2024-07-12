package pl.sknikod.kodemysearch.infrastructure.module.material;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pl.sknikod.kodemysearch.exception.ExceptionUtil;
import pl.sknikod.kodemysearch.infrastructure.module.material.model.MaterialIndexData;
import pl.sknikod.kodemysearch.infrastructure.module.material.model.MaterialSearchFields;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class MaterialSearchUseCase {
    private final MaterialSearchHandler materialSearchHandler;
    private final MaterialSearchMapper mapper;

    public Page<MaterialPageable> search(MaterialSearchFields searchFields, Pageable pageRequest) {
        return materialSearchHandler.search(mapToSearchCriteria(searchFields, pageRequest))
                .map(page -> new PageImpl<>(mapper.map(page.getContent()), page.getPageable(), page.getTotalElements()))
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    private SearchCriteria mapToSearchCriteria(@NonNull MaterialSearchFields searchFields, @NonNull Pageable pageable) {
        var criteria = new SearchCriteria(searchFields.getPhrase(), pageable);

        if (Objects.nonNull(searchFields.getId()))
            criteria.addPhraseField(new SearchCriteria.PhraseField(
                    "id", searchFields.getId().toString(), false, false
            ));
        if (Objects.nonNull(searchFields.getTitle()))
            criteria.addPhraseField(new SearchCriteria.PhraseField(
                    "title", searchFields.getTitle(), false, false
            ));
        if (Objects.nonNull(searchFields.getStatus()))
            criteria.addPhraseField(new SearchCriteria.PhraseField(
                    "status", searchFields.getStatus(), false, false
            ));
        if (Objects.nonNull(searchFields.getCreatedBy()))
            criteria.addPhraseField(new SearchCriteria.PhraseField(
                    "createdBy", searchFields.getCreatedBy(), false, false
            ));
        if (Objects.nonNull(searchFields.getSectionId()))
            criteria.addPhraseField(new SearchCriteria.PhraseField(
                    "sectionId", searchFields.getSectionId().toString(), false, false
            ));
        var categoryIds = searchFields.getCategoryIds();
        if (Objects.nonNull(categoryIds) && categoryIds.length != 0) {
            criteria.addPhraseField(new SearchCriteria.PhraseField(
                    "categoryIds", StringUtils.join(categoryIds, ' '), false, false
            ));
        }
        var tagIds = searchFields.getTagIds();
        if (Objects.nonNull(tagIds) && tagIds.length != 0) {
            criteria.addPhraseField(new SearchCriteria.PhraseField(
                    "tagIds", StringUtils.join(searchFields.getTagIds(), ' '), false, false
            ));
        }

        if (Objects.nonNull(searchFields.getCreatedDateFrom()) || Objects.nonNull(searchFields.getCreatedDateTo()))
            criteria.addRangeField(new SearchCriteria.RangeField<>(
                    "createdDate", searchFields.getCreatedDateFrom(), searchFields.getCreatedDateTo()
            ));
        if (Objects.nonNull(searchFields.getMinAvgGrade()) || Objects.nonNull(searchFields.getMaxAvgGrade()))
            criteria.addRangeField(new SearchCriteria.RangeField<>(
                    "avgGrade", searchFields.getMinAvgGrade(), searchFields.getMaxAvgGrade()
            ));

        return criteria;
    }

    private boolean filterByAvgGrade(MaterialSearchFields searchFields, MaterialPageable m) {
        double gradeAvg = m.avgGrade;
        return (searchFields.getMinAvgGrade() == null || gradeAvg >= searchFields.getMinAvgGrade()) &&
                (searchFields.getMaxAvgGrade() == null || gradeAvg <= searchFields.getMaxAvgGrade());
    }

    @Getter
    @RequiredArgsConstructor
    public enum MaterialSortField {
        ID("id"),
        TITLE("title"),
        DESCRIPTION("description"),
        STATUS("status"),
        IS_ACTIVE("isActive"),
        AVG_GRADE("avgGrade"),
        AUTHOR("author"),
        CREATED_DATE("createdDate"),
        SECTION_ID("sectionId"),
        CATEGORY_ID("categoryId");

        private final String field;
    }

    @Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
    public interface MaterialSearchMapper {
        List<MaterialPageable> map(List<MaterialIndexData> indexData);

        @Mapping(target = "isActive", source = "active")
        @Mapping(target = "author", source = "user")
        MaterialPageable map(MaterialIndexData indexData);
    }

    public record MaterialPageable(
            Long id,
            String title,
            String description,
            MaterialStatus status,
            boolean isActive,
            double avgGrade,
            AuthorDetails author,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
            Date createdDate,
            Long sectionId,
            Long categoryId,
            List<TagDetails> tags
    ) {
        public enum MaterialStatus {
            APPROVED,
            PENDING,
            REJECTED,
            EDITED,
            BANNED,
            DRAFT,
            BAN_REQUESTED,
            DEPRECATION_REQUEST,
            DEPRECATED,
            DELETED
        }

        public record TagDetails(Long id, String name) {
        }

        public record AuthorDetails(Long id, String username) {
        }
    }
}
