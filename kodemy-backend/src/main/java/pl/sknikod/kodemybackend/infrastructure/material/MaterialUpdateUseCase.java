package pl.sknikod.kodemybackend.infrastructure.material;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.configuration.RabbitConfig;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.infrastructure.auth.AuthService;
import pl.sknikod.kodemybackend.infrastructure.common.EntityDao;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Category;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Technology;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Type;
import pl.sknikod.kodemybackend.infrastructure.common.repository.GradeRepository;
import pl.sknikod.kodemybackend.infrastructure.common.repository.MaterialRepository;
import pl.sknikod.kodemybackend.infrastructure.material.rest.MaterialUpdateRequest;
import pl.sknikod.kodemybackend.infrastructure.material.rest.MaterialUpdateResponse;

import java.util.Set;

import static pl.sknikod.kodemybackend.infrastructure.common.entity.Material.MaterialStatus.APPROVED;
import static pl.sknikod.kodemybackend.infrastructure.common.entity.Material.MaterialStatus.PENDING;

@Component
@AllArgsConstructor
public class MaterialUpdateUseCase {
    private final MaterialRepository materialRepository;
    private final MaterialUpdateMapper updateMaterialMapper;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitConfig.QueueProperties queueProperties;
    private final MaterialRabbitMapper rabbitMapper;
    private final GradeRepository gradeRepository;
    private final EntityDao entityDao;
    private final AuthService authService;

    public MaterialUpdateResponse execute(Long materialId, MaterialUpdateRequest body) {
        // TODO refactor
        Material existingMaterial = entityDao.findMaterialById(materialId);
        return Option.of(body)
                .map(materialUpdateRequest -> updateMaterialMapper.map(
                        existingMaterial,
                        body,
                        entityDao.findCategoryById(body.getCategoryId()),
                        entityDao.findTypeById(body.getTypeId()),
                        entityDao.findTechnologySetByIds(body.getTechnologiesIds())
                ))
                .map(this::updateStatus)
                .map(materialRepository::save)
                .peek(this::executeOpenSearchIndex)
                .peek(this::executeNotificationStatus)
                .map(updateMaterialMapper::map)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Material.class));
    }

    private Material updateStatus(Material material) {
        material.setStatus(
                authService.getPrincipal().getAuthorities()
                        .contains(new SimpleGrantedAuthority("CAN_AUTO_APPROVED_MATERIAL"))
                        ? APPROVED : PENDING
        );
        return material;
    }

    private void executeNotificationStatus(Material material) {
        // TODO notification
    }

    private void executeOpenSearchIndex(Material material) {
        rabbitTemplate.convertAndSend(
                queueProperties.get("m-update").getName(),
                "",
                rabbitMapper.map(
                        material,
                        gradeRepository.findAverageGradeByMaterialId(material.getId())
                )
        );
    }

    @Mapper(componentModel = "spring")
    public interface MaterialUpdateMapper {
        default Material map(Material existingMaterial, MaterialUpdateRequest body, Category category, Type type, Set<Technology> technologies) {
            existingMaterial.setActive(true);
            existingMaterial.setStatus(PENDING);
            existingMaterial.setTitle(body.getTitle());
            existingMaterial.setDescription(body.getDescription());
            existingMaterial.setLink(body.getLink());
            existingMaterial.setCategory(category);
            existingMaterial.setType(type);
            existingMaterial.setTechnologies(technologies);
            return existingMaterial;
        }

        MaterialUpdateResponse map(Material material);
    }
}