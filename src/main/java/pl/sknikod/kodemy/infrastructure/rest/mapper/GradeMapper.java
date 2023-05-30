package pl.sknikod.kodemy.infrastructure.rest.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.infrastructure.model.grade.Grade;
import pl.sknikod.kodemy.infrastructure.rest.model.request.MaterialAddGradeRequest;
import pl.sknikod.kodemy.infrastructure.rest.model.response.SingleGradeResponse;

import java.util.Set;

@Component
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD,
        uses = {
                UserMapper.class
        }
)
public abstract class GradeMapper {
    @Autowired
    protected UserMapper userMapper;

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "user", expression = "java(userMapper.mapUserFromContext())"),
            @Mapping(target = "material", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "createdDate", ignore = true),
            @Mapping(target = "lastModifiedBy", ignore = true),
            @Mapping(target = "lastModifiedDate", ignore = true)
    })
    public abstract Grade map(MaterialAddGradeRequest body);

    public abstract Set<SingleGradeResponse> map(Set<Grade> grades);

    @Mapping(target = "createdBy", source = "user")
    public abstract SingleGradeResponse map(Grade grade);
}
