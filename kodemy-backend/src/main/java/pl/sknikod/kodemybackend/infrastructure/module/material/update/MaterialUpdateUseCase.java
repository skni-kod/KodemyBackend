package pl.sknikod.kodemybackend.infrastructure.module.material.update;

import io.swagger.v3.oas.annotations.media.Schema;
import io.vavr.control.Try;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Category;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Tag;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Type;
import pl.sknikod.kodemybackend.infrastructure.database.handler.*;
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialRabbitProducer;
import pl.sknikod.kodemycommon.exception.InternalError500Exception;
import pl.sknikod.kodemycommon.exception.content.ExceptionUtil;
import pl.sknikod.kodemycommon.security.AuthFacade;
import pl.sknikod.kodemycommon.security.UserPrincipal;

import java.util.List;
import java.util.Set;

import static pl.sknikod.kodemybackend.infrastructure.database.entity.Material.MaterialStatus.APPROVED;
import static pl.sknikod.kodemybackend.infrastructure.database.entity.Material.MaterialStatus.PENDING;

@AllArgsConstructor
public class MaterialUpdateUseCase {
    private final MaterialUpdateMapper updateMaterialMapper;
    private final CategoryRepositoryHandler categoryRepositoryHandler;
    private final TypeRepositoryHandler typeRepositoryHandler;
    private final TagRepositoryHandler tagRepositoryHandler;
    private final MaterialRepositoryHandler materialRepositoryHandler;
    private final MaterialRabbitProducer materialProducer;
    private final GradeRepositoryHandler gradeRepositoryHandler;
    private static final SimpleGrantedAuthority CAN_AUTO_APPROVED_MATERIAL =
            new SimpleGrantedAuthority("CAN_AUTO_APPROVED_MATERIAL");

    public MaterialUpdateResponse update(Long materialId, MaterialUpdateRequest body) {
        var userPrincipal = AuthFacade.getCurrentUserPrincipal()
                .orElseThrow(InternalError500Exception::new);
        var material = materialRepositoryHandler.findById(materialId)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
        var category = categoryRepositoryHandler.findById(body.categoryId)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
        var type = typeRepositoryHandler.findById(body.typeId)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
        var tags = tagRepositoryHandler.findAllByIdIn(body.tagsIds)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
        var avgGrade = gradeRepositoryHandler.findAvgGradeByMaterial(material.getId())
                .getOrElseThrow(ExceptionUtil::throwIfFailure);

        return Try.of(() -> updateEntity(material, body, userPrincipal, category, type, tags))
                .flatMap(materialRepositoryHandler::save)
                .peek(entity -> materialProducer.sendUpdatedMaterialToReindex(
                        MaterialRabbitProducer.Message.map(material, avgGrade)))
                .map(updateMaterialMapper::map)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    private Material updateEntity(
            Material material,
            MaterialUpdateRequest body,
            UserPrincipal userPrincipal,
            Category category,
            Type type,
            Set<Tag> tags
    ) {
        material.setActive(true);
        material.setTitle(body.getTitle());
        material.setDescription(body.getDescription());
        material.setLink(body.getLink());
        material.setCategory(category);
        material.setType(type);
        material.setTags(tags);
        material.setStatus(validateStatus(userPrincipal.getAuthorities(), material.getStatus()));
        return material;
    }

    private Material.MaterialStatus validateStatus(Set<SimpleGrantedAuthority> authorities, Material.MaterialStatus status) {
        var isAdmin = authorities.contains(CAN_AUTO_APPROVED_MATERIAL);
        return switch (status) {
            case APPROVED -> isAdmin ? status : PENDING;
            case PENDING -> isAdmin ? APPROVED : status;
            default -> PENDING;
        };
    }

    @Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
    public interface MaterialUpdateMapper {
        MaterialUpdateResponse map(Material material);
    }

    public record MaterialUpdateResponse(
            Long id,
            String title,
            String description,
            String link,
            MaterialUpdateUseCase.MaterialUpdateResponse.BaseDetails category,
            MaterialUpdateUseCase.MaterialUpdateResponse.BaseDetails type,
            Set<BaseDetails> tags
    ) {
        public record BaseDetails(
                Long id,
                String name
        ) {
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
}