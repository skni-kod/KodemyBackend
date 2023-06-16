package pl.sknikod.kodemy.infrastructure.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import pl.sknikod.kodemy.infrastructure.model.grade.Grade;
import pl.sknikod.kodemy.infrastructure.model.user.User;
import pl.sknikod.kodemy.infrastructure.rest.UserService;
import pl.sknikod.kodemy.infrastructure.rest.model.MaterialAddGradeRequest;
import pl.sknikod.kodemy.infrastructure.rest.model.SingleGradeResponse;

import java.util.Set;

@Mapper(componentModel = "spring", uses = {
        UserMapper.class
})
public abstract class GradeMapper {
    @Autowired
    protected UserService userService;

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "user", source = "request", qualifiedByName = "mapUser"),
            @Mapping(target = "material", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "createdDate", ignore = true),
            @Mapping(target = "lastModifiedBy", ignore = true),
            @Mapping(target = "lastModifiedDate", ignore = true)
    })
    public abstract Grade map(MaterialAddGradeRequest request);

    @Named(value = "mapUser")
    protected User mapUser(MaterialAddGradeRequest request) {
        return userService.getUserFromContext();
    }

    @Mapping(target = "creator", source = "user")
    public abstract SingleGradeResponse map(Grade grade);

    public abstract Set<SingleGradeResponse> map(Set<Grade> grades);
}
