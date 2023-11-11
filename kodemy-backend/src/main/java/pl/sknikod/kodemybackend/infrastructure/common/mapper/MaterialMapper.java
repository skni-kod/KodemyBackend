package pl.sknikod.kodemybackend.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.material.rest.SingleMaterialResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MaterialMapper {

    @Mappings(value = {
            @Mapping(target = "averageGrade", ignore = true),
            @Mapping(target = "gradeStats", ignore = true),
    })
    SingleMaterialResponse map(Material material);

    default SingleMaterialResponse map(Material material, Double averageGrade, List<Long> gradeStats) {
        var response = map(material);
        response.setAverageGrade(averageGrade);
        response.setGradeStats(gradeStats);
        return response;
    }
}
