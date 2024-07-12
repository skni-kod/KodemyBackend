package pl.sknikod.kodemybackend.infrastructure.database.handler;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.exception.ExceptionPattern;
import pl.sknikod.kodemybackend.exception.structure.NotFoundException;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.database.repository.MaterialRepository;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.SearchFields;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MaterialRepositoryHandler {
    private final MaterialRepository materialRepository;

    public Try<Material> findById(Long id) {
        return Try.of(() -> materialRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(ExceptionPattern.ENTITY_NOT_FOUND_BY_PARAM, Material.class, "id", id)))
                .onFailure(th -> log.error(th.getMessage(), th));
    }

    public Try<Material> save(Material material) {
        return Try.of(() -> materialRepository.save(material))
                .onFailure(th -> log.error("Cannot save material", th));
    }

    public Try<Page<Object[]>> searchMaterialsWithAvgGrades(
            SearchFields searchFields,
            List<Material.MaterialStatus> statuses,
            Long userId,
            PageRequest pageRequest
    ) {
        return Try.of(() -> materialRepository.searchMaterialsWithAvgGrades(
                searchFields.getId(),
                searchFields.getPhrase(),
                statuses,
                searchFields.getCreatedBy(),
                searchFields.getSectionId(),
                searchFields.getCategoryIds(),
                searchFields.getTagIds(),
                userId,
                searchFields.getCreatedDateFrom(),
                searchFields.getCreatedDateTo(),
                searchFields.getMinAvgGrade(),
                searchFields.getMaxAvgGrade(),
                pageRequest
        ));
    }
}
