package pl.sknikod.kodemybackend.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import pl.sknikod.kodemybackend.infrastructure.database.Grade;
import pl.sknikod.kodemybackend.infrastructure.module.grade.MaterialGradeService;

import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GradeMapper {
    MaterialGradeService.GradePageable map(Grade grade);

    Set<MaterialGradeService.GradePageable> map(Set<Grade> grades);
}
