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
import pl.sknikod.kodemycommons.exception.content.ExceptionUtil;
import pl.sknikod.kodemysearch.infrastructure.dao.MaterialSearchDao;
import pl.sknikod.kodemysearch.infrastructure.module.material.model.MaterialIndexData;
import pl.sknikod.kodemysearch.infrastructure.module.material.model.MaterialFilterSearchParams;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class MaterialSearchService {
    private final MaterialSearchDao materialSearchDao;
    private final MaterialSearchMapper mapper;

    public Page<MaterialPageable> search(MaterialFilterSearchParams filterSearchParams, Pageable pageRequest) {
        return materialSearchDao.search(mapToSearchCriteria(filterSearchParams, pageRequest))
                .map(page -> new PageImpl<>(mapper.map(page.getContent()), page.getPageable(), page.getTotalElements()))
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    private SearchCriteria mapToSearchCriteria(@NonNull MaterialFilterSearchParams filterSearchParams, @NonNull Pageable pageable) {
        var criteria = new SearchCriteria(filterSearchParams.getPhrase(), pageable);

        if (Objects.nonNull(filterSearchParams.getId()))
            criteria.addPhraseField(new SearchCriteria.PhraseField(
                    "id", filterSearchParams.getId().toString(), false, false
            ));
        if (Objects.nonNull(filterSearchParams.getTitle()))
            criteria.addPhraseField(new SearchCriteria.PhraseField(
                    "title", filterSearchParams.getTitle(), false, false
            ));
        if (Objects.nonNull(filterSearchParams.getStatus()))
            criteria.addPhraseField(new SearchCriteria.PhraseField(
                    "status", filterSearchParams.getStatus(), false, false
            ));
        if (Objects.nonNull(filterSearchParams.getCreatedBy()))
            criteria.addPhraseField(new SearchCriteria.PhraseField(
                    "createdBy", filterSearchParams.getCreatedBy(), false, false
            ));
        if (Objects.nonNull(filterSearchParams.getSectionId()))
            criteria.addPhraseField(new SearchCriteria.PhraseField(
                    "sectionId", filterSearchParams.getSectionId().toString(), false, false
            ));
        var categoryIds = filterSearchParams.getCategoryIds();
        if (Objects.nonNull(categoryIds) && categoryIds.length != 0) {
            criteria.addPhraseField(new SearchCriteria.PhraseField(
                    "categoryIds", StringUtils.join(categoryIds, ' '), false, false
            ));
        }
        var tagIds = filterSearchParams.getTagIds();
        if (Objects.nonNull(tagIds) && tagIds.length != 0) {
            criteria.addPhraseField(new SearchCriteria.PhraseField(
                    "tagIds", StringUtils.join(filterSearchParams.getTagIds(), ' '), false, false
            ));
        }

        if (Objects.nonNull(filterSearchParams.getCreatedDateFrom()) || Objects.nonNull(filterSearchParams.getCreatedDateTo()))
            criteria.addRangeField(new SearchCriteria.RangeField<>(
                    "createdDate", filterSearchParams.getCreatedDateFrom(), filterSearchParams.getCreatedDateTo()
            ));
        if (Objects.nonNull(filterSearchParams.getMinAvgGrade()) || Objects.nonNull(filterSearchParams.getMaxAvgGrade()))
            criteria.addRangeField(new SearchCriteria.RangeField<>(
                    "avgGrade", filterSearchParams.getMinAvgGrade(), filterSearchParams.getMaxAvgGrade()
            ));

        return criteria;
    }

    private boolean filterByAvgGrade(MaterialFilterSearchParams filterSearchParams, MaterialPageable m) {
        double gradeAvg = m.avgGrade;
        return (filterSearchParams.getMinAvgGrade() == null || gradeAvg >= filterSearchParams.getMinAvgGrade()) &&
                (filterSearchParams.getMaxAvgGrade() == null || gradeAvg <= filterSearchParams.getMaxAvgGrade());
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
