package pl.sknikod.kodemybackend.infrastructure.material;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.configuration.RabbitConfig;
import pl.sknikod.kodemybackend.configuration.SecurityConfig;
import pl.sknikod.kodemybackend.exception.structure.NotFoundException;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Category;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Technology;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Type;
import pl.sknikod.kodemybackend.infrastructure.common.repository.*;
import pl.sknikod.kodemybackend.infrastructure.material.rest.MaterialUpdateRequest;
import pl.sknikod.kodemybackend.infrastructure.material.rest.MaterialUpdateResponse;

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
    private final RabbitTemplate rabbitTemplate;
    private final RabbitConfig.QueueProperties queueProperties;
    private final MaterialRabbitMapper rabbitMapper;
    private final GradeRepository gradeRepository;

    public MaterialUpdateResponse execute(Long materialId, MaterialUpdateRequest body) {
        Material existingMaterial = materialRepository.findById(materialId).orElseThrow(() ->
                new NotFoundException(NotFoundException.Format.ENTITY_ID, Material.class, materialId)
        );
        return Option.of(body)
                .map(updateMaterialMapper::map)
                .map(material -> updatingMissingMaterialProperties(body, existingMaterial))
                .map(materialRepository::save)
                .peek(this::executeOpenSearchIndex)
                .peek(this::executeNotificationStatus)
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

    private void executeNotificationStatus(Material material) {
        Option.of(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof SecurityConfig.JwtUserDetails)
                .map(principal -> (SecurityConfig.JwtUserDetails) principal);
        // TODO notification
    }

    private void executeOpenSearchIndex(Material material) {
        rabbitTemplate.convertAndSend(
                queueProperties.get("m-created").getName(),
                "",
                rabbitMapper.map(
                        material,
                        gradeRepository.findAverageGradeByMaterialId(material.getId())
                )
        );
    }
}