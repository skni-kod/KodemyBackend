package pl.sknikod.kodemy.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pl.sknikod.kodemy.infrastructure.common.entity.Grade;
import pl.sknikod.kodemy.infrastructure.material.rest.MaterialAddGradeRequest;
import pl.sknikod.kodemy.infrastructure.material.rest.SingleGradeResponse;

import java.util.Set;

@Mapper(componentModel = "spring", uses = {
        UserMapper.class
})
public interface GradeMapper {
    @Mappings(value = {
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "user", ignore = true),
            @Mapping(target = "value", source = "grade"),
            @Mapping(target = "material", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "createdDate", ignore = true),
            @Mapping(target = "lastModifiedBy", ignore = true),
            @Mapping(target = "lastModifiedDate", ignore = true)
    })
    Grade map(MaterialAddGradeRequest request);

    @Mapping(target = "creator", source = "user")
    SingleGradeResponse map(Grade grade);

    Set<SingleGradeResponse> map(Set<Grade> grades);
}
