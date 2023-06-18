package pl.sknikod.kodemy.infrastructure.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import pl.sknikod.kodemy.infrastructure.model.grade.Grade;
import pl.sknikod.kodemy.infrastructure.model.grade.GradeRepository;
import pl.sknikod.kodemy.infrastructure.model.material.Material;
import pl.sknikod.kodemy.infrastructure.rest.model.SingleMaterialResponse;

import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class CategoryMaterialMapper {
    @Autowired
    protected GradeRepository gradeRepository;

    public abstract Set<SingleMaterialResponse> map(Set<Material> materials);

    @Mappings(value = {
            @Mapping(target = "creator", source = "user"),
            @Mapping(target = "averageGrade", source = "material", qualifiedByName = "mapAvgGrade")
    })
    public abstract SingleMaterialResponse map(Material material);

    @Named(value = "mapAvgGrade")
    protected Double mapAvgGrade(Material material) {
        return gradeRepository.findAllByMaterialId(material.getId())
                .stream()
                .mapToDouble(Grade::getValue)
                .average()
                .orElse(0.0);
    }
}
