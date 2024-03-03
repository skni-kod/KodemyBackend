package pl.sknikod.kodemybackend.infrastructure.material;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.common.repository.GradeRepository;
import pl.sknikod.kodemybackend.infrastructure.common.repository.MaterialRepository;
import pl.sknikod.kodemybackend.infrastructure.common.repository.TechnologyRepository;
import pl.sknikod.kodemybackend.infrastructure.material.rest.SearchFields;

import javax.validation.constraints.NotNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class MaterialAdminGetUseCase {
    private final TechnologyRepository technologyRepository;
    private final MaterialRepository materialRepository;
    private final GradeRepository gradeRepository;

    public Page<MaterialPageable> manage(@NotNull SearchFields searchFields, PageRequest page) {
        Page<Object[]> materials = materialRepository.searchMaterialsWithAvgGrades(
                searchFields.getId(),
                searchFields.getPhrase(),
                searchFields.getStatus(),
                searchFields.getCreatedBy(),
                searchFields.getSectionId(),
                searchFields.getCategoryIds(),
                searchFields.getTechnologyIds(),
                null,
                searchFields.getCreatedDateFrom(),
                searchFields.getCreatedDateTo(),
                page
        );
        return new PageImpl<>(
                materials.stream()
                        .map(material -> this.map((Material) material[0], (Double) material[1]))
                        .toList(),
                page,
                materials.getTotalElements()
        );
    }

    private MaterialPageable map(Material material, Double avgGrade) {
        var output = MaterialPageable.builder();
        var type = material.getType();
        output.type(new MaterialPageable.TypeDetails(
                type.getId(), type.getName()
        ));
        var technologies = material.getTechnologies()
                .stream()
                .map(tech -> new MaterialPageable.TechnologyDetails(tech.getId(), tech.getName()))
                .toList();
        output.technologies(technologies);
        var author = material.getAuthor();
        output.author(new MaterialPageable.AuthorDetails(
                author.getId(), author.getUsername()
        ));

        output.id(material.getId());
        output.title(material.getTitle());
        output.description(material.getDescription());
        output.link(material.getLink());
        output.status(material.getStatus());
        output.createdDate(material.getCreatedDate());
        output.gradeAvg(avgGrade);
        return output.build();
    }
}
