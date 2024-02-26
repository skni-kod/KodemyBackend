package pl.sknikod.kodemybackend.infrastructure.common;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.exception.structure.NotFoundException;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Category;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Technology;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Type;
import pl.sknikod.kodemybackend.infrastructure.common.repository.CategoryRepository;
import pl.sknikod.kodemybackend.infrastructure.common.repository.MaterialRepository;
import pl.sknikod.kodemybackend.infrastructure.common.repository.TechnologyRepository;
import pl.sknikod.kodemybackend.infrastructure.common.repository.TypeRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EntityDao {
    private final CategoryRepository categoryRepository;
    private final TypeRepository typeRepository;
    private final MaterialRepository materialRepository;
    private final TechnologyRepository technologyRepository;

    public Category findCategoryById(Long categoryId) {
        return Option.ofOptional(categoryRepository.findById(categoryId))
                .getOrElseThrow(() ->
                        new NotFoundException(NotFoundException.Format.ENTITY_ID, Category.class, categoryId)
                );
    }

    public Type findTypeById(Long typeId) {
        return Option.ofOptional(typeRepository.findById(typeId))
                .getOrElseThrow(() ->
                        new NotFoundException(NotFoundException.Format.ENTITY_ID, Type.class, typeId)
                );
    }

    public Material findMaterialById(Long materialId) {
        return Option.ofOptional(materialRepository.findById(materialId))
                .getOrElseThrow(() ->
                        new NotFoundException(NotFoundException.Format.ENTITY_ID, Material.class, materialId)
                );
    }

    public Technology findTechnologyById(Long technologyId) {
        return Option.ofOptional(technologyRepository.findById(technologyId))
                .getOrElseThrow(() ->
                        new NotFoundException(NotFoundException.Format.ENTITY_ID, Technology.class, technologyId)
                );
    }

    public Set<Technology> findTechnologySetByIds(Set<Long> technologiesIds) {
        return technologiesIds
                .stream()
                .map(this::findTechnologyById)
                .collect(Collectors.toSet());
    }
}
