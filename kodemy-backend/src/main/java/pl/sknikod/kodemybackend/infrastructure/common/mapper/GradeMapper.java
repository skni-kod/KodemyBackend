package pl.sknikod.kodemybackend.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Grade;
import pl.sknikod.kodemybackend.infrastructure.module.grade.MaterialGradeUseCase;

import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GradeMapper {
    MaterialGradeUseCase.GradePageable map(Grade grade);

    Set<MaterialGradeUseCase.GradePageable> map(Set<Grade> grades);
}
