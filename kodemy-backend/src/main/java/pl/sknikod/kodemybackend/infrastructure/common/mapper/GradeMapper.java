package pl.sknikod.kodemybackend.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemybackend.configuration.SecurityConfig;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Author;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Grade;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.material.rest.MaterialAddGradeRequest;
import pl.sknikod.kodemybackend.infrastructure.material.rest.SingleGradeResponse;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface GradeMapper {
    default Grade map(MaterialAddGradeRequest request, Material material, SecurityConfig.JwtUserDetails author) {
        var grade = new Grade();
        grade.setMaterial(material);
        grade.setAuthor(Author.map(author));
        grade.setValue(Double.valueOf(request.getGrade()));
        return grade;
    }

    SingleGradeResponse map(Grade grade);

    Set<SingleGradeResponse> map(Set<Grade> grades);
}
