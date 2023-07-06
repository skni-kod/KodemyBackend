package pl.sknikod.kodemy.infrastructure.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import pl.sknikod.kodemy.infrastructure.model.entity.Material;
import pl.sknikod.kodemy.infrastructure.model.entity.MaterialStatus;
import pl.sknikod.kodemy.infrastructure.rest.UserService;
import pl.sknikod.kodemy.infrastructure.rest.model.MaterialCreateRequest;
import pl.sknikod.kodemy.infrastructure.rest.model.MaterialCreateResponse;

@Mapper(componentModel = "spring")
public abstract class MaterialCreateMapper {
    @Mappings(value = {
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "active", constant = "true"),
            @Mapping(target = "status", source = "body", qualifiedByName = "mapStatus"),
            @Mapping(target = "type", ignore = true),
            @Mapping(target = "category", ignore = true),
            @Mapping(target = "technologies", ignore = true),
            @Mapping(target = "user", ignore = true),
            @Mapping(target = "grades", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "createdDate", ignore = true),
            @Mapping(target = "lastModifiedBy", ignore = true),
            @Mapping(target = "lastModifiedDate", ignore = true)
    })
    public abstract Material map(MaterialCreateRequest body);

    @Named(value = "mapStatus")
    protected MaterialStatus mapStatus(MaterialCreateRequest body) {
        return UserService.checkPrivilege("CAN_AUTO_APPROVED_MATERIAL") ? MaterialStatus.APPROVED : MaterialStatus.PENDING;
    }

    public abstract MaterialCreateResponse map(Material material);
}
