package pl.sknikod.kodemybackend.infrastructure.module.material;

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
import org.springframework.stereotype.Service;
import pl.sknikod.kodemybackend.infrastructure.database.Category;
import pl.sknikod.kodemybackend.infrastructure.database.Material;
import pl.sknikod.kodemybackend.infrastructure.database.Tag;
import pl.sknikod.kodemybackend.infrastructure.database.Type;
import pl.sknikod.kodemybackend.infrastructure.store.CategoryStore;
import pl.sknikod.kodemybackend.infrastructure.store.MaterialStore;
import pl.sknikod.kodemybackend.infrastructure.store.TagStore;
import pl.sknikod.kodemybackend.infrastructure.store.TypeStore;
import pl.sknikod.kodemycommons.exception.content.ExceptionUtil;
import pl.sknikod.kodemycommons.security.AuthFacade;

import java.util.List;
import java.util.Set;

import static pl.sknikod.kodemybackend.infrastructure.database.Material.MaterialStatus.APPROVED;
import static pl.sknikod.kodemybackend.infrastructure.database.Material.MaterialStatus.PENDING;

@Service
@AllArgsConstructor
public class MaterialUpdateService {
    private final MaterialUpdateMapper updateMaterialMapper;
    private final CategoryStore categoryStore;
    private final TypeStore typeStore;
    private final TagStore tagStore;
    private final MaterialStore materialStore;
    private static final SimpleGrantedAuthority CAN_AUTO_APPROVED_MATERIAL =
            new SimpleGrantedAuthority("CAN_AUTO_APPROVED_MATERIAL");

    public MaterialUpdateResponse update(Long materialId, MaterialUpdateRequest body) {
        var material = materialStore.findById(materialId, true)
                .map(MaterialStore.FindByIdObject::material)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
        var category = categoryStore.findById(body.categoryId)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
        var type = typeStore.findById(body.typeId)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
        var tags = tagStore.findAllByIdIn(body.tagsIds)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);

        return Try.of(() -> updateEntity(material, body, category, type, tags))
                .flatMap(materialStore::update)
                .map(updateMaterialMapper::map)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    private Material updateEntity(
            Material material,
            MaterialUpdateRequest body,
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
        material.setStatus(validateStatus(AuthFacade.getCurrentUserPrincipal().get().getAuthorities(), material.getStatus()));
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
            MaterialUpdateService.MaterialUpdateResponse.BaseDetails category,
            MaterialUpdateService.MaterialUpdateResponse.BaseDetails type,
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