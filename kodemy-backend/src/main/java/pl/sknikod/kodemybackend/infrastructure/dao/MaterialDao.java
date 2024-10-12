package pl.sknikod.kodemybackend.infrastructure.dao;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.infrastructure.database.Material;
import pl.sknikod.kodemybackend.infrastructure.database.MaterialRepository;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.FilterSearchParams;
import pl.sknikod.kodemycommons.exception.NotFound404Exception;
import pl.sknikod.kodemycommons.exception.content.ExceptionMsgPattern;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MaterialDao {
    private final MaterialRepository materialRepository;

    public Try<Material> findById(Long id) {
        return Try.of(() -> materialRepository.findById(id)
                        .orElseThrow(() -> new NotFound404Exception(ExceptionMsgPattern.ENTITY_NOT_FOUND_BY_PARAM, Material.class, "id", id)))
                .onFailure(th -> log.error(th.getMessage(), th));
    }

    public Try<Material> save(Material material) {
        return Try.of(() -> materialRepository.save(material))
                .onFailure(th -> log.error("Cannot save material", th));
    }

    public Try<Page<Object[]>> searchMaterialsWithAvgGrades(
            FilterSearchParams filterSearchParams,
            List<Material.MaterialStatus> statuses,
            Long userId,
            PageRequest pageRequest
    ) {
        return Try.of(() -> materialRepository.searchMaterialsWithAvgGrades(
                filterSearchParams.getId(),
                filterSearchParams.getPhrase(),
                statuses,
                filterSearchParams.getCreatedBy(),
                filterSearchParams.getSectionId(),
                filterSearchParams.getCategoryIds(),
                filterSearchParams.getTagIds(),
                userId,
                filterSearchParams.getCreatedDateFrom(),
                filterSearchParams.getCreatedDateTo(),
                filterSearchParams.getMinAvgGrade(),
                filterSearchParams.getMaxAvgGrade(),
                pageRequest
        ));
    }
}
