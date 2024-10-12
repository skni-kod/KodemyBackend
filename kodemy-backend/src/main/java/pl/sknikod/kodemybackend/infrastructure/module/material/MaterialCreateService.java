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
import pl.sknikod.kodemybackend.infrastructure.database.Category;
import pl.sknikod.kodemybackend.infrastructure.database.Material;
import pl.sknikod.kodemybackend.infrastructure.database.Tag;
import pl.sknikod.kodemybackend.infrastructure.database.Type;
import pl.sknikod.kodemybackend.infrastructure.dao.CategoryDao;
import pl.sknikod.kodemybackend.infrastructure.dao.MaterialDao;
import pl.sknikod.kodemybackend.infrastructure.dao.TagDao;
import pl.sknikod.kodemybackend.infrastructure.dao.TypeDao;
import pl.sknikod.kodemybackend.infrastructure.module.material.producer.MaterialCreatedProducer;
import pl.sknikod.kodemycommons.exception.InternalError500Exception;
import pl.sknikod.kodemycommons.exception.content.ExceptionUtil;
import pl.sknikod.kodemycommons.security.AuthFacade;
import pl.sknikod.kodemycommons.security.UserPrincipal;

import java.util.List;
import java.util.Set;

import static pl.sknikod.kodemybackend.infrastructure.database.Material.MaterialStatus.APPROVED;
import static pl.sknikod.kodemybackend.infrastructure.database.Material.MaterialStatus.PENDING;

@Slf4j
@RequiredArgsConstructor
public class MaterialCreateService {
    private final MaterialDao materialDao;
    private final TypeDao typeDao;
    private final MaterialCreateMapper createMaterialMapper;
    private final CategoryDao categoryDao;
    private final TagDao tagDao;
    private final MaterialCreatedProducer materialCreatedProducer;

    public MaterialCreateResponse create(MaterialCreateRequest body) {
        var userPrincipal = AuthFacade.getCurrentUserPrincipal()
                .orElseThrow(InternalError500Exception::new);
        var category = categoryDao.findById(body.categoryId)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
        var type = typeDao.findById(body.typeId)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
        var tags = tagDao.findAllByIdIn(body.tagsIds)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);

        return Try.of(() -> this.toMaterial(body, userPrincipal, category, type, tags))
                .flatMap(materialDao::save)
                .peek(material -> {
                    if (material.getStatus() == APPROVED)
                        materialCreatedProducer.publish(MaterialCreatedProducer.Message.map(material, userPrincipal));
                })
                .map(createMaterialMapper::map)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    private Material toMaterial(
            MaterialCreateRequest body,
            UserPrincipal userPrincipal,
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
        material.setUserId(userPrincipal.getId());
        var isApprovedMaterial = userPrincipal.getAuthorities().contains(new SimpleGrantedAuthority("CAN_AUTO_APPROVED_MATERIAL"))
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
