package pl.sknikod.kodemy.infrastructure.rest.mapper;

import org.mapstruct.*;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.infrastructure.model.grade.Grade;
import pl.sknikod.kodemy.infrastructure.model.material.Material;
import pl.sknikod.kodemy.infrastructure.rest.model.response.SingleMaterialResponse;

import java.util.Set;

@Component
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD,
        uses = {
                UserMapper.class, TechnologyMapper.class, TypeMapper.class
        }
)
public abstract class CategoryMaterialMapper {

    public abstract Set<SingleMaterialResponse> map(Set<Material> materials);

    @Mappings(value = {
            @Mapping(target = "createdBy", source = "user"),
            @Mapping(target = "averageGrade", source = "grades", qualifiedByName = "mapGradeSetToAverageGrade")
    })
    public abstract SingleMaterialResponse map(Material material);

    @Named(value = "mapGradeSetToAverageGrade")
    protected Double mapGradeSetToAverageGrade(Set<Grade> grades) {
        return grades.stream().mapToDouble(Grade::getGrade).average().orElse(0.0);
    }
}
