package pl.sknikod.kodemy.rest.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.grade.Grade;
import pl.sknikod.kodemy.material.Material;
import pl.sknikod.kodemy.material.MaterialStatus;
import pl.sknikod.kodemy.rest.request.MaterialAddGradeRequest;
import pl.sknikod.kodemy.rest.request.MaterialCreateRequest;
import pl.sknikod.kodemy.rest.response.MaterialCreateResponse;
import pl.sknikod.kodemy.technology.Technology;
import pl.sknikod.kodemy.user.User;
import pl.sknikod.kodemy.user.UserPrincipal;

import java.util.List;
import java.util.Set;

@Component
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD,
        uses = {
                GradeMapper.class, UserMapper.class
        }
)
public abstract class GradeMapper {
    @Autowired
    protected UserMapper userMapper;

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "grade", source = "grade"),
            @Mapping(target = "user", expression = "java(userMapper.mapUserFromContext())"),
            @Mapping(target = "material", ignore = true)
    })
    public abstract Grade map(MaterialAddGradeRequest body);

}
