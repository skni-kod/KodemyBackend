package pl.sknikod.kodemybackend.infrastructure.material;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.common.repository.MaterialRepository;
import pl.sknikod.kodemybackend.infrastructure.material.rest.SearchFields;

import javax.validation.constraints.NotNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class MaterialAdminGetUseCase {
    private final MaterialRepository materialRepository;
    private final MaterialGetUseCase materialGetUseCase;

    public Page<MaterialPageable> manage(@NotNull SearchFields searchFields, PageRequest page) {
        Page<Object[]> materials = materialRepository.searchMaterialsWithAvgGrades(
                searchFields.getId(),
                searchFields.getPhrase(),
                searchFields.getStatuses(),
                searchFields.getCreatedBy(),
                searchFields.getSectionId(),
                searchFields.getCategoryIds(),
                searchFields.getTagIds(),
                null,
                searchFields.getCreatedDateFrom(),
                searchFields.getCreatedDateTo(),
                page
        );
        return new PageImpl<>(
                materials.stream()
                        .map(material -> materialGetUseCase.map((Material) material[0], (Double) material[1]))
                        .toList(),
                page,
                materials.getTotalElements()
        );
    }
}
