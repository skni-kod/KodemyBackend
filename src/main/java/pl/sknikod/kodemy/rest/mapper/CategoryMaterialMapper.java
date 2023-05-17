package pl.sknikod.kodemy.rest.mapper;

import org.mapstruct.*;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.grade.Grade;
import pl.sknikod.kodemy.material.Material;
import pl.sknikod.kodemy.rest.response.SingleMaterialResponse;

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
