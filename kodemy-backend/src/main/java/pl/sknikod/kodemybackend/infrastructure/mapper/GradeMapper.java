package pl.sknikod.kodemybackend.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import pl.sknikod.kodemybackend.infrastructure.database.Grade;
import pl.sknikod.kodemybackend.infrastructure.module.grade.GradeService;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GradeMapper {
    default GradeService.GradePageable map(Grade grade, String username) {
        return new GradeService.GradePageable(
                grade.getId(),
                grade.getValue(),
                new GradeService.GradePageable.AuthorDetails(
                        grade.getUserId(),
                        username
                )
        );
    }
}
