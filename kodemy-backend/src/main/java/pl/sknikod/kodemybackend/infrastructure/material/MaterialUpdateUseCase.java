package pl.sknikod.kodemybackend.infrastructure.material;

import io.swagger.v3.oas.annotations.media.Schema;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import org.mapstruct.Mapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.configuration.RabbitConfig;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.infrastructure.common.ContextUtil;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Category;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Tag;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Type;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.MaterialMapper;
import pl.sknikod.kodemybackend.infrastructure.common.repository.*;
import pl.sknikod.kodemybackend.infrastructure.material.rest.SingleMaterialResponse;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.List;
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
    private final MaterialMapper materialMapper;
    private final CategoryRepository categoryRepository;
    private final TypeRepository typeRepository;
    private final TagRepository tagRepository;
    private final ContextUtil contextUtil;

    public MaterialUpdateResponse update(Long materialId, MaterialUpdateRequest body) {
        Material existingMaterial = materialRepository.findMaterialById(materialId);
        return Option.of(body)
                .map(materialUpdateRequest -> updateMaterialMapper.map(
                        existingMaterial,
                        body,
                        categoryRepository.findCategoryById(body.getCategoryId()),
                        typeRepository.findTypeById(body.getTypeId()),
                        tagRepository.findTagsByIdIn(body.getTagsIds())
                ))
                .map(this::updateStatus)
                .map(materialRepository::save)
                .peek(this::executeOpenSearchIndex)
                .peek(this::executeNotificationStatus)
                .map(updateMaterialMapper::map)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Material.class));
    }

    private Material updateStatus(Material material) {
        material.setStatus(contextUtil.getCurrentUserPrincipal().getAuthorities()
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
                queueProperties.get("m-updated").getName(),
                "",
                rabbitMapper.map(
                        material,
                        gradeRepository.findAvgGradeByMaterialId(material.getId())
                )
        );
    }

    @Mapper(componentModel = "spring")
    public interface MaterialUpdateMapper {
        default Material map(Material existingMaterial, MaterialUpdateRequest body, Category category, Type type, Set<Tag> tags) {
            existingMaterial.setActive(true);
            existingMaterial.setStatus(PENDING);
            existingMaterial.setTitle(body.getTitle());
            existingMaterial.setDescription(body.getDescription());
            existingMaterial.setLink(body.getLink());
            existingMaterial.setCategory(category);
            existingMaterial.setType(type);
            existingMaterial.setTags(tags);
            return existingMaterial;
        }

        MaterialUpdateResponse map(Material material);
    }

    @Value
    public static class MaterialUpdateResponse {
        Long id;
        String title;
        String description;
        String link;
        BaseDetails category;
        BaseDetails type;
        Set<BaseDetails> tags;

        @Value
        public static class BaseDetails {
            Long id;
            String name;
        }
    }

    @Data
    @AllArgsConstructor
    public static class MaterialUpdateRequest {
        @NotNull
        @Schema(example = "Title of the material")
        private String title;

        @Schema(example = "Description of the material")
        private String description;

        @Pattern(regexp = "^https?://.*", message = "Link must start with http:// or https://")
        @Schema(example = "https://www.example.com/material/java-programming")
        private String link;

        @NotNull
        @Positive(message = "Category ID must be > 0")
        private Long categoryId;

        @NotNull
        @Positive(message = "Type ID must be > 0")
        private Long typeId;

        private List<@NotNull @Positive(message = "Tag ID must be > 0") Long> tagsIds;
    }

    public SingleMaterialResponse changeStatus(Long materialId, Material.MaterialStatus status) {
        return Option.of(materialRepository.findMaterialById(materialId))
                .peek(material -> material.setStatus(status))
                .map(materialRepository::save)
                .map(materialMapper::map)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Material.class));
    }
}