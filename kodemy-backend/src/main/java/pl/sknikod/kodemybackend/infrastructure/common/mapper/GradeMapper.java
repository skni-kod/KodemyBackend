package pl.sknikod.kodemybackend.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemybackend.configuration.SecurityConfig;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Author;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Grade;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.material.MaterialGradeUseCase;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface GradeMapper {
    default Grade map(MaterialGradeUseCase.MaterialAddGradeRequest request, Material material, SecurityConfig.UserPrincipal author) {
        var grade = new Grade();
        grade.setMaterial(material);
        grade.setAuthor(Author.map(author));
        grade.setValue(Double.valueOf(request.getGrade()));
        return grade;
    }

    MaterialGradeUseCase.GradePageable map(Grade grade);

    Set<MaterialGradeUseCase.GradePageable> map(Set<Grade> grades);
}
