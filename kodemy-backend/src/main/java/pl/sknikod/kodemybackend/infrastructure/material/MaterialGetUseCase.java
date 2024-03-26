package pl.sknikod.kodemybackend.infrastructure.material;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.MaterialMapper;
import pl.sknikod.kodemybackend.infrastructure.common.repository.GradeRepository;
import pl.sknikod.kodemybackend.infrastructure.common.repository.MaterialRepository;
import pl.sknikod.kodemybackend.infrastructure.material.rest.SearchFields;
import pl.sknikod.kodemybackend.infrastructure.material.rest.SingleMaterialResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class MaterialGetUseCase {
    private final GradeRepository gradeRepository;
    private final MaterialRepository materialRepository;
    private final MaterialMapper materialMapper;

    public SingleMaterialResponse showDetails(Long materialId) {
        return Option.of(materialRepository.findMaterialById(materialId))
                .map(material -> materialMapper.map(
                        material,
                        gradeRepository.findAvgGradeByMaterialId(materialId),
                        fetchGradeStats(materialId))
                )
                .getOrElseThrow(() -> new ServerProcessingException(
                        ServerProcessingException.Format.PROCESS_FAILED, Material.class
                ));
    }

    public Page<MaterialPageable> getPersonalMaterials(Long authorId, SearchFields searchFields, PageRequest pageRequest) {
        Page<Object[]> materials = materialRepository.searchMaterialsWithAvgGrades(
                searchFields.getId(),
                searchFields.getPhrase(),
                searchFields.getStatuses(),
                searchFields.getCreatedBy(),
                searchFields.getSectionId(),
                searchFields.getCategoryIds(),
                searchFields.getTagIds(),
                authorId,
                searchFields.getCreatedDateFrom(),
                searchFields.getCreatedDateTo(),
                pageRequest
        );
        //todo user check
        return new PageImpl<>(
                materials.stream()
                        .map(material -> this.map((Material) material[0], (Double) material[1]))
                        .toList(),
                pageRequest,
                materials.getTotalElements()
        );
    }

    MaterialPageable map(Material material, Double avgGrade) {
        var output = MaterialPageable.builder();
        var type = material.getType();
        output.type(new MaterialPageable.TypeDetails(
                type.getId(), type.getName()
        ));
        var tags = material.getTags()
                .stream()
                .map(tag -> new MaterialPageable.TagDetails(tag.getId(), tag.getName()))
                .toList();
        output.tags(tags);
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

    private List<Long> fetchGradeStats(Long materialId) {
        return Stream.iterate(1.0, i -> i <= 5.0, i -> i + 1.0)
                .map(i -> gradeRepository.countAllByMaterialIdAndValue(materialId, i))
                .collect(Collectors.toList());
    }
}
