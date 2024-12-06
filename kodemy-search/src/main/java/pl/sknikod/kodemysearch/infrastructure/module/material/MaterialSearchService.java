package pl.sknikod.kodemysearch.infrastructure.module.material;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemycommons.exception.content.ExceptionUtil;
import pl.sknikod.kodemysearch.infrastructure.module.material.model.MaterialIndexData;
import pl.sknikod.kodemysearch.infrastructure.module.material.model.MaterialPageable;
import pl.sknikod.kodemysearch.infrastructure.rest.MaterialControllerDefinition;
import pl.sknikod.kodemysearch.infrastructure.store.MaterialSearchStore;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaterialSearchService {
    private final MaterialSearchStore materialSearchStore;
    private final MaterialSearchMapper mapper;

    public Page<MaterialPageable> search(MaterialControllerDefinition.MaterialFilterSearchParams filterSearchParams, Pageable pageRequest) {
        return materialSearchStore.search(createSearchCriteria(filterSearchParams, pageRequest))
                .map(page -> new PageImpl<>(mapper.map(page.getContent()), page.getPageable(), page.getTotalElements()))
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    private SearchCriteria createSearchCriteria(@NonNull MaterialControllerDefinition.MaterialFilterSearchParams filterSearchParams, @NonNull Pageable pageable) {
        var criteria = new SearchCriteria(filterSearchParams.getPhrase(), pageable);

        if (Objects.nonNull(filterSearchParams.getId()))
            criteria.addPhraseField(new SearchCriteria.PhraseField(
                    "id", filterSearchParams.getId().toString(), false, false
            ));
        if (Objects.nonNull(filterSearchParams.getSectionId()))
            criteria.addPhraseField(new SearchCriteria.PhraseField(
                    "sectionId", filterSearchParams.getSectionId().toString(), false, false
            ));
        var categoryIds = filterSearchParams.getCategoryIds();
        if (Objects.nonNull(categoryIds) && !categoryIds.isEmpty()) {
            criteria.addArrayField(new SearchCriteria.ArrayField(
                    "categoryId", categoryIds.stream().map(String::valueOf).toList()));
        }
        if (Objects.nonNull(filterSearchParams.getMinAvgGrade()) || Objects.nonNull(filterSearchParams.getMaxAvgGrade()))
            criteria.addRangeField(new SearchCriteria.RangeField<>(
                    "avgGrade", filterSearchParams.getMinAvgGrade(), filterSearchParams.getMaxAvgGrade()
            ));

        return criteria;
    }

    @Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
    public interface MaterialSearchMapper {
        List<MaterialPageable> map(List<MaterialIndexData> indexData);

        @Mapping(target = "isActive", source = "active")
        MaterialPageable map(MaterialIndexData indexData);
    }
}
