package pl.sknikod.kodemy.dto.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.category.Category;
import pl.sknikod.kodemy.category.CategoryRepository;
import pl.sknikod.kodemy.dto.MaterialCreateRequest;
import pl.sknikod.kodemy.dto.MaterialCreateResponse;
import pl.sknikod.kodemy.exception.ResourceNotFoundException;
import pl.sknikod.kodemy.material.Material;
import pl.sknikod.kodemy.material.MaterialStatus;
import pl.sknikod.kodemy.role.Role;
import pl.sknikod.kodemy.technology.Technology;
import pl.sknikod.kodemy.technology.TechnologyRepository;
import pl.sknikod.kodemy.type.Type;
import pl.sknikod.kodemy.type.TypeRepository;

import java.util.List;
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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "status", expression = "java(getMaterialStatus())")
    @Mapping(target = "type", source = "typeId", qualifiedByName = "mapType")
    @Mapping(target = "category", source = "categoryId", qualifiedByName = "mapCategory")
    @Mapping(target = "technologies", source = "technologiesIds", qualifiedByName = "mapTechnologiesIds")
    @Mapping(target = "user", expression = "java(userMapper.getCurrentUser())")
    @Mapping(target = "grades", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    public abstract Material map(MaterialCreateRequest body);

    @Mapping(target = "technologies", source = "technologies", qualifiedByName = "mapTechnologiesSetToList")
    @Mapping(target = "createdBy", source = "user")
    public abstract MaterialCreateResponse map(Material material);

    @Named("mapType")
    protected Type mapType(Long id) {
        return typeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type not found"));
    }

    @Named("mapCategory")
    protected Category mapCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    @Named("mapTechnologiesIds")
    protected Set<Technology> mapTechnologiesIds(Set<Long> technologiesIds) {
        return technologiesIds
                .stream()
                .map(id -> technologyRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Technology not found"))
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

    protected MaterialStatus getMaterialStatus(){
        var userRoles = userMapper
                .getCurrentUser()
                .getRoles()
                .stream()
                .map(Role::getName)
                .filter(name -> name.contains("ADMIN"))
                .toList();

        return (userRoles.size() > 0) ? MaterialStatus.APPROVED : MaterialStatus.UNAPPROVED;
    }
}
