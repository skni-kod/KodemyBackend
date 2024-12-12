package pl.sknikod.kodemybackend.infrastructure.module.material;

import io.swagger.v3.oas.annotations.media.Schema;
import io.vavr.control.Try;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import pl.sknikod.kodemycommons.util.PrincipalUtil;

import java.util.List;
import java.util.Set;

import static pl.sknikod.kodemybackend.infrastructure.database.Material.MaterialStatus.APPROVED;
import static pl.sknikod.kodemybackend.infrastructure.database.Material.MaterialStatus.PENDING;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialCreateService {
    private final MaterialStore materialStore;
    private final TypeStore typeStore;
    private final MaterialCreateMapper materialCreateMapper;
    private final CategoryStore categoryStore;
    private final TagStore tagStore;
    private final PrincipalUtil principalUtil;

    public MaterialCreateResponse create(MaterialCreateRequest body) {
        var category = categoryStore.findById(body.categoryId)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
        var type = typeStore.findById(body.typeId)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
        var tags = tagStore.findAllByIdIn(body.tagsIds)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);

        return Try.of(() -> this.toMaterial(body, category, type, tags))
                .flatMap(materialStore::save)
                .map(materialCreateMapper::map)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    private Material toMaterial(
            MaterialCreateRequest body,
            Category category,
            Type type,
            Set<Tag> tags
    ) {
        var material = new Material();
        material.setTitle(body.getTitle());
        material.setDescription(body.getDescription());
        material.setLink(body.getLink());
        material.setActive(true);
        material.setCategory(category);
        material.setType(type);
        material.setTags(tags);
        var user = principalUtil.getPrincipal().get();
        material.setUserId(user.getId());
        var isApprovedMaterial = user.getAuthorities().contains(new SimpleGrantedAuthority("CAN_AUTO_APPROVED_MATERIAL"))
                ? APPROVED : PENDING;
        material.setStatus(isApprovedMaterial);
        return material;
    }

    @Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
    public interface MaterialCreateMapper {
        MaterialCreateResponse map(Material material);
    }

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    public static class MaterialCreateRequest {
        @NotNull
        @Schema(example = "Title of the material")
        private String title;

        @Schema(example = "Description of the material")
        private String description;

        @Pattern(regexp = "^https?://.*", message = "Link must start with http:// or https://")
        @Schema(example = "https://www.example.com/material/java-programming")
        private String link;

        @NotNull
        @Positive(message = "Type ID must be > 0")
        private Long typeId;

        @NotNull
        @Positive(message = "Category ID must be > 0")
        private Long categoryId;

        private List<@NotNull @Positive(message = "Tag ID must be > 0") Long> tagsIds;
    }

    public record MaterialCreateResponse(
            Long id,
            String title,
            @Enumerated(EnumType.STRING) Material.MaterialStatus status
    ) {
    }
}
