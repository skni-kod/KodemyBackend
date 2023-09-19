package pl.sknikod.kodemy.infrastructure.material;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import pl.sknikod.kodemy.infrastructure.common.entity.Material;
import pl.sknikod.kodemy.infrastructure.common.entity.MaterialStatus;
import pl.sknikod.kodemy.infrastructure.material.rest.MaterialUpdateRequest;
import pl.sknikod.kodemy.infrastructure.material.rest.MaterialUpdateResponse;
import pl.sknikod.kodemy.infrastructure.user.UserService;

@Mapper(componentModel = "spring")
public interface MaterialUpdateMapper {
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
    Material map(MaterialUpdateRequest body);


    @Named(value = "mapStatus")
    default MaterialStatus mapStatus(MaterialUpdateRequest body) {
        return UserService.checkPrivilege("CAN_AUTO_APPROVED_MATERIAL") ? MaterialStatus.APPROVED : MaterialStatus.PENDING;
    }

    MaterialUpdateResponse map(Material material);
}
