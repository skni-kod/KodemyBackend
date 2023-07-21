package pl.sknikod.kodemy.infrastructure.rest;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.exception.structure.NotFoundException;
import pl.sknikod.kodemy.exception.structure.ServerProcessingException;
import pl.sknikod.kodemy.infrastructure.model.entity.*;
import pl.sknikod.kodemy.infrastructure.model.repository.CategoryRepository;
import pl.sknikod.kodemy.infrastructure.model.repository.MaterialRepository;
import pl.sknikod.kodemy.infrastructure.model.repository.TechnologyRepository;
import pl.sknikod.kodemy.infrastructure.model.repository.TypeRepository;
import pl.sknikod.kodemy.infrastructure.rest.mapper.MaterialCreateMapper;
import pl.sknikod.kodemy.infrastructure.rest.model.MaterialCreateRequest;
import pl.sknikod.kodemy.infrastructure.rest.model.MaterialCreateResponse;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class MaterialCreateUseCase {

    private final MaterialRepository materialRepository;
    private final MaterialCreateMapper createMaterialMapper;
    private final OpenSearchService openSearchService;
    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final TypeRepository typeRepository;
    private final TechnologyRepository technologyRepository;
    private final NotificationService notificationService;

    public MaterialCreateResponse execute(MaterialCreateRequest body){

        return Option.of(body)
                .map(createMaterialMapper::map)
                .map(material -> attachDetails(body, material))
                .map(materialRepository::save)
                .peek(material -> {
                    openSearchService.createIndexIfNotExists(OpenSearchService.Info.MATERIAL);
                    openSearchService.indexMaterial(material);
                })
                .map(this::checkApproval)
                .map(createMaterialMapper::map)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Material.class));

    }

    private Material attachDetails(MaterialCreateRequest body, Material material) {
        material.setUser(userService.getContextUser());
        material.setCategory(categoryRepository.findById(body.getCategoryId()).orElseThrow(() ->
                new NotFoundException(NotFoundException.Format.ENTITY_ID, Category.class, body.getCategoryId())
        ));
        material.setTechnologies(mapTechnologies(body.getTechnologiesIds()));
        material.setType(typeRepository.findById(body.getTypeId()).orElseThrow(() ->
                new NotFoundException(NotFoundException.Format.ENTITY_ID, Type.class, body.getTypeId())
        ));
        return material;
    }

    private Set<Technology> mapTechnologies(Set<Long> technologiesIds) {
        return technologiesIds
                .stream()
                .map(id -> technologyRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(NotFoundException.Format.ENTITY_ID, Technology.class, id))
                )
                .collect(Collectors.toSet());
    }

    private Material checkApproval(Material material) {
        if (!UserService.checkPrivilege("CAN_AUTO_APPROVED_MATERIAL"))
            notificationService.sendNotificationToAdmins(
                    NotificationTitle.MATERIAL_APPROVAL_REQUEST.getDesc(),
                    String.format("{\"id\":%d, \"title\":\"%s\"}", material.getId(), material.getTitle())
            );
        return material;
    }

}
