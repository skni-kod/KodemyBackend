package pl.sknikod.kodemy.infrastructure.material;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.exception.structure.NotFoundException;
import pl.sknikod.kodemy.exception.structure.ServerProcessingException;
import pl.sknikod.kodemy.infrastructure.common.entity.*;
import pl.sknikod.kodemy.infrastructure.common.repository.CategoryRepository;
import pl.sknikod.kodemy.infrastructure.common.repository.MaterialRepository;
import pl.sknikod.kodemy.infrastructure.common.repository.TechnologyRepository;
import pl.sknikod.kodemy.infrastructure.common.repository.TypeRepository;
import pl.sknikod.kodemy.infrastructure.material.rest.MaterialCreateRequest;
import pl.sknikod.kodemy.infrastructure.material.rest.MaterialCreateResponse;
import pl.sknikod.kodemy.infrastructure.notification.NotificationAsyncService;
import pl.sknikod.kodemy.infrastructure.user.UserService;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class MaterialCreateUseCase {

    private final MaterialRepository materialRepository;
    private final MaterialCreateMapper createMaterialMapper;
    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final TypeRepository typeRepository;
    private final TechnologyRepository technologyRepository;
    private final MaterialAsyncService materialAsyncService;
    private final NotificationAsyncService notificationAsyncService;

    public MaterialCreateResponse execute(MaterialCreateRequest body) {
        return Option.of(body)
                .map(createMaterialMapper::map)
                .map(material -> initializeMissingMaterialProperties(body, material))
                .map(materialRepository::save)
                .peek(this::openSearchIndex)
                .peek(this::performApprovalCheck)
                .map(createMaterialMapper::map)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Material.class));
    }

    private Material initializeMissingMaterialProperties(MaterialCreateRequest body, Material material) {
        material.setUser(userService.getContextUser());
        material.setCategory(categoryRepository.findById(body.getCategoryId()).orElseThrow(() ->
                new NotFoundException(NotFoundException.Format.ENTITY_ID, Category.class, body.getCategoryId())
        ));
        material.setTechnologies(fetchTechnologies(body.getTechnologiesIds()));
        material.setType(typeRepository.findById(body.getTypeId()).orElseThrow(() ->
                new NotFoundException(NotFoundException.Format.ENTITY_ID, Type.class, body.getTypeId())
        ));
        return material;
    }

    private Set<Technology> fetchTechnologies(Set<Long> technologiesIds) {
        return technologiesIds
                .stream()
                .map(id -> technologyRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(NotFoundException.Format.ENTITY_ID, Technology.class, id))
                )
                .collect(Collectors.toSet());
    }

    private void openSearchIndex(Material material) {
        materialAsyncService.sendToIndex(material);
    }

    private void performApprovalCheck(Material material) {
        if (!UserService.checkPrivilege("CAN_AUTO_APPROVED_MATERIAL"))
            notificationAsyncService.sendToAdmins(
                    NotificationTitle.MATERIAL_APPROVAL_REQUEST.getDesc(),
                    String.format("{\"id\":%d, \"title\":\"%s\"}", material.getId(), material.getTitle())
            );
    }
}
