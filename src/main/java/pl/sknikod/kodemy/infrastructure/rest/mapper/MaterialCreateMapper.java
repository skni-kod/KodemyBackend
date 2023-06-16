package pl.sknikod.kodemy.infrastructure.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import pl.sknikod.kodemy.exception.structure.NotFoundException;
import pl.sknikod.kodemy.infrastructure.model.category.Category;
import pl.sknikod.kodemy.infrastructure.model.category.CategoryRepository;
import pl.sknikod.kodemy.infrastructure.model.material.Material;
import pl.sknikod.kodemy.infrastructure.model.material.MaterialStatus;
import pl.sknikod.kodemy.infrastructure.model.user.User;
import pl.sknikod.kodemy.infrastructure.model.user.UserPrincipal;
import pl.sknikod.kodemy.infrastructure.rest.UserService;
import pl.sknikod.kodemy.infrastructure.rest.model.MaterialCreateRequest;
import pl.sknikod.kodemy.infrastructure.rest.model.MaterialCreateResponse;

@Mapper(componentModel = "spring", uses = {
        TypeMapper.class, TechnologyMapper.class
})
public abstract class MaterialCreateMapper {
    @Autowired
    protected UserService userService;
    @Autowired
    protected CategoryRepository categoryRepository;

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "active", constant = "true"),
            @Mapping(target = "status", source = "body", qualifiedByName = "mapStatus"),
            @Mapping(target = "type", source = "typeId"),
            @Mapping(target = "category", source = "categoryId", qualifiedByName = "mapCategory"),
            @Mapping(target = "technologies", source = "technologiesIds"),
            @Mapping(target = "user", source = "body", qualifiedByName = "mapUser"),
            @Mapping(target = "grades", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "createdDate", ignore = true),
            @Mapping(target = "lastModifiedBy", ignore = true),
            @Mapping(target = "lastModifiedDate", ignore = true)
    })
    public abstract Material map(MaterialCreateRequest body);

    @Named(value = "mapStatus")
    protected MaterialStatus mapStatus(MaterialCreateRequest body) {
        return UserPrincipal.checkPrivilege("CAN_AUTO_APPROVED_MATERIAL") ? MaterialStatus.APPROVED : MaterialStatus.PENDING;
    }

    @Named(value = "mapCategory")
    protected Category mapCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException(NotFoundException.Format.entityId, Category.class, categoryId));
    }

    @Named(value = "mapUser")
    protected User mapUser(MaterialCreateRequest body) {
        return userService.getUserFromContext();
    }

    public abstract MaterialCreateResponse map(Material material);
}
