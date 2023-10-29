package pl.sknikod.kodemybackend.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Grade;
import pl.sknikod.kodemybackend.infrastructure.material.rest.MaterialAddGradeRequest;
import pl.sknikod.kodemybackend.infrastructure.material.rest.SingleGradeResponse;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface GradeMapper {
    @Mappings(value = {
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "author", ignore = true),
            @Mapping(target = "value", source = "grade"),
            @Mapping(target = "material", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "createdDate", ignore = true),
            @Mapping(target = "lastModifiedBy", ignore = true),
            @Mapping(target = "lastModifiedDate", ignore = true)
    })
    Grade map(MaterialAddGradeRequest request);

    @Mapping(target = "creator.id", source = "author.id")
    @Mapping(target = "creator.username", source = "author.name")
    SingleGradeResponse map(Grade grade);

    Set<SingleGradeResponse> map(Set<Grade> grades);
}
