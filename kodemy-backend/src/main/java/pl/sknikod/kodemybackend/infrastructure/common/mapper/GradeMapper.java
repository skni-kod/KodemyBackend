package pl.sknikod.kodemybackend.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Grade;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.material.rest.MaterialAddGradeRequest;
import pl.sknikod.kodemybackend.infrastructure.material.rest.SingleGradeResponse;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface GradeMapper {
    default Grade map(MaterialAddGradeRequest request, Material material, Long userId){
        var grade = new Grade();
        grade.setValue(Double.valueOf(request.getGrade()));
        grade.setMaterial(material);
        return grade;
    }

    SingleGradeResponse map(Grade grade);

    Set<SingleGradeResponse> map(Set<Grade> grades);
}
