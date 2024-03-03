package pl.sknikod.kodemybackend.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Grade;
import pl.sknikod.kodemybackend.infrastructure.material.MaterialGradeUseCase;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface GradeMapper {
    MaterialGradeUseCase.GradePageable map(Grade grade);

    Set<MaterialGradeUseCase.GradePageable> map(Set<Grade> grades);
}
