package pl.sknikod.kodemy.infrastructure.rest.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.infrastructure.model.material.Material;
import pl.sknikod.kodemy.infrastructure.model.material.MaterialStatus;
import pl.sknikod.kodemy.infrastructure.rest.model.request.MaterialCreateRequest;
import pl.sknikod.kodemy.infrastructure.rest.model.response.MaterialCreateResponse;
import pl.sknikod.kodemy.infrastructure.model.technology.Technology;
import pl.sknikod.kodemy.infrastructure.model.user.UserPrincipal;

import java.util.List;
import java.util.Set;

@Component
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD,
        uses = {
                TypeMapper.class, CategoryMapper.class, TechnologyMapper.class, UserMapper.class
        }
)
public abstract class MaterialCreationMapper {
    @Autowired
    protected UserMapper userMapper;

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "active", constant = "true"),
            @Mapping(target = "status", expression = "java(getNewMaterialStatus())"),
            @Mapping(target = "type", source = "typeId"),
            @Mapping(target = "category", source = "categoryId"),
            @Mapping(target = "technologies", source = "technologiesIds"),
            @Mapping(target = "user", expression = "java(userMapper.mapUserFromContext())"),
            @Mapping(target = "grades", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "createdDate", ignore = true),
            @Mapping(target = "lastModifiedBy", ignore = true),
            @Mapping(target = "lastModifiedDate", ignore = true)
    })
    public abstract Material map(MaterialCreateRequest body);

    @Mapping(target = "createdBy", source = "user")
    public abstract MaterialCreateResponse map(Material material);

    public abstract List<MaterialCreateResponse.TechnologyDetails> map(Set<Technology> technologies);

    protected MaterialStatus getNewMaterialStatus() {
        return UserPrincipal.checkPrivilege("CAN_AUTO_APPROVED_MATERIAL") ?
                MaterialStatus.APPROVED : MaterialStatus.PENDING;
    }
}
