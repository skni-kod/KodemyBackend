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
import pl.sknikod.kodemy.infrastructure.material.rest.MaterialUpdateRequest;
import pl.sknikod.kodemy.infrastructure.material.rest.MaterialUpdateResponse;
import pl.sknikod.kodemy.infrastructure.notification.NotificationAsyncService;
import pl.sknikod.kodemy.infrastructure.user.UserService;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class MaterialUpdateUseCase {
    private final MaterialRepository materialRepository;
    private final MaterialUpdateMapper updateMaterialMapper;
    private final CategoryRepository categoryRepository;
    private final TypeRepository typeRepository;
    private final TechnologyRepository technologyRepository;
    private final MaterialAsyncService materialAsyncService;
    private final NotificationAsyncService notificationAsyncService;

    public MaterialUpdateResponse execute(Long materialId, MaterialUpdateRequest body) {
        Material existingMaterial = materialRepository.findById(materialId).orElseThrow(() ->
                new NotFoundException(NotFoundException.Format.ENTITY_ID, Material.class, materialId)
        );
        return Option.of(body)
                .map(updateMaterialMapper::map)
                .map(material -> updatingMissingMaterialProperties(body, existingMaterial))
                .map(materialRepository::save)
                .peek(this::openSearchReindex)
                .peek(this::performApprovalCheck)
                .map(updateMaterialMapper::map)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Material.class));
    }

    private Material updatingMissingMaterialProperties(MaterialUpdateRequest body, Material existingMaterial) {
        existingMaterial.setTitle(body.getTitle());
        existingMaterial.setDescription(body.getDescription());
        existingMaterial.setLink(body.getLink());
        existingMaterial.setCategory(categoryRepository.findById(body.getCategoryId()).orElseThrow(() ->
                new NotFoundException(NotFoundException.Format.ENTITY_ID, Category.class, body.getCategoryId())
        ));
        existingMaterial.setTechnologies(fetchTechnologies(body.getTechnologiesIds()));
        existingMaterial.setType(typeRepository.findById(body.getTypeId()).orElseThrow(() ->
                new NotFoundException(NotFoundException.Format.ENTITY_ID, Type.class, body.getTypeId())
        ));
        return existingMaterial;
    }

    private Set<Technology> fetchTechnologies(Set<Long> technologiesIds) {
        return technologiesIds
                .stream()
                .map(id -> technologyRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(NotFoundException.Format.ENTITY_ID, Technology.class, id))
                )
                .collect(Collectors.toSet());
    }

    private void openSearchReindex(Material material) {
        materialAsyncService.sendToReindex(material);
    }

    private void performApprovalCheck(Material material) {
        if (!UserService.checkPrivilege("CAN_AUTO_APPROVED_MATERIAL"))
            notificationAsyncService.sendToAdmins(
                    NotificationTitle.MATERIAL_APPROVAL_REQUEST.getDesc(),
                    String.format("{\"id\":%d, \"title\":\"%s\"}", material.getId(), material.getTitle())
            );
    }
}