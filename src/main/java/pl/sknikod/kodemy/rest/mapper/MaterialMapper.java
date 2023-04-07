package pl.sknikod.kodemy.rest.mapper;

import io.vavr.control.Option;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.category.Category;
import pl.sknikod.kodemy.category.CategoryRepository;
import pl.sknikod.kodemy.rest.request.MaterialCreateRequest;
import pl.sknikod.kodemy.rest.response.MaterialCreateResponse;
import pl.sknikod.kodemy.exception.general.NotFoundException;
import pl.sknikod.kodemy.material.Material;
import pl.sknikod.kodemy.material.MaterialStatus;
import pl.sknikod.kodemy.technology.Technology;
import pl.sknikod.kodemy.technology.TechnologyRepository;
import pl.sknikod.kodemy.type.Type;
import pl.sknikod.kodemy.type.TypeRepository;
import pl.sknikod.kodemy.user.User;
import pl.sknikod.kodemy.user.UserPrincipal;
import pl.sknikod.kodemy.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public abstract class MaterialMapper {
    @Autowired
    protected TypeRepository typeRepository;
    @Autowired
    protected TechnologyRepository technologyRepository;
    @Autowired
    protected CategoryRepository categoryRepository;
    @Autowired
    protected UserMapper userMapper;
    @Autowired
    private UserRepository userRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "status", expression = "java(getNewMaterialStatus())")
    @Mapping(target = "type", source = "typeId", qualifiedByName = "mapType")
    @Mapping(target = "category", source = "categoryId", qualifiedByName = "mapCategory")
    @Mapping(target = "technologies", source = "technologiesIds", qualifiedByName = "mapTechnologiesIds")
    @Mapping(target = "user", expression = "java(mapUserFromContext())")
    @Mapping(target = "grades", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    public abstract Material map(MaterialCreateRequest body);

    @Mapping(target = "technologies", source = "technologies", qualifiedByName = "mapTechnologiesSetToList")
    @Mapping(target = "createdBy", source = "user", qualifiedByName = "mapUserToCreatedBy")
    public abstract MaterialCreateResponse map(Material material);

    @Named("mapType")
    protected Type mapType(Long id) {
        return typeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Type not found"));
    }

    @Named("mapCategory")
    protected Category mapCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }
    @Named("mapTechnologiesIds")
    protected Set<Technology> mapTechnologiesIds(Set<Long> technologiesIds) {
        return technologiesIds
                .stream()
                .map(id -> technologyRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Technology not found"))
                )
                .collect(Collectors.toSet());
    }

    @Named("mapTechnologiesSetToList")
    protected List<MaterialCreateResponse.TechnologyDetails> mapTechnologiesSetToList(Set<Technology> technologies) {
        return technologies
                .stream()
                .map(technology -> new MaterialCreateResponse.TechnologyDetails(
                                technology.getId(), technology.getName()
                        )
                )
                .toList();
    }

    @Named("mapUserToCreatedBy")
    @Mapping(target = "name", source = "username")
    public abstract MaterialCreateResponse.UserDetails map(User user);

    protected User mapUserFromContext() {
        return Option.of(UserPrincipal.getCurrentSessionUser())
                .map(UserPrincipal::getId)
                .map(userRepository::findById)
                .map(Optional::get)
                .getOrElseThrow(() -> new NotFoundException("Session user not found"));
    }

    protected MaterialStatus getNewMaterialStatus(){
        boolean canAutoApprovedMaterial = Option.of(UserPrincipal.getCurrentSessionUser())
                .map(UserPrincipal::getAuthorities)
                .map(ArrayList::new)
                .map(perms -> perms.contains(new SimpleGrantedAuthority("CAN_AUTO_APPROVED_MATERIAL")))
                .getOrElse(false);
        return canAutoApprovedMaterial ? MaterialStatus.APPROVED : MaterialStatus.PENDING;
    }
}
