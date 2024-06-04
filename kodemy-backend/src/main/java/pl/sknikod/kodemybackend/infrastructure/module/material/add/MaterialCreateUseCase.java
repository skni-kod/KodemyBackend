package pl.sknikod.kodemybackend.infrastructure.module.material.add;

import io.swagger.v3.oas.annotations.media.Schema;
import io.vavr.control.Try;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.exception.ExceptionUtil;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Category;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Tag;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Type;
import pl.sknikod.kodemybackend.infrastructure.database.handler.CategoryRepositoryHandler;
import pl.sknikod.kodemybackend.infrastructure.database.handler.MaterialRepositoryHandler;
import pl.sknikod.kodemybackend.infrastructure.database.handler.TagRepositoryHandler;
import pl.sknikod.kodemybackend.infrastructure.database.handler.TypeRepositoryHandler;
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialRabbitProducer;
import pl.sknikod.kodemybackend.util.auth.AuthFacade;
import pl.sknikod.kodemybackend.util.auth.UserPrincipal;

import java.util.List;
import java.util.Set;

import static pl.sknikod.kodemybackend.infrastructure.database.entity.Material.MaterialStatus.APPROVED;
import static pl.sknikod.kodemybackend.infrastructure.database.entity.Material.MaterialStatus.PENDING;

@Slf4j
@RequiredArgsConstructor
public class MaterialCreateUseCase {
    private final MaterialRepositoryHandler materialRepositoryHandler;
    private final TypeRepositoryHandler typeRepositoryHandler;
    private final MaterialCreateMapper createMaterialMapper;
    private final CategoryRepositoryHandler categoryRepositoryHandler;
    private final TagRepositoryHandler tagRepositoryHandler;
    private final MaterialRabbitProducer materialProducer;
    private static final SimpleGrantedAuthority CAN_AUTO_APPROVED_MATERIAL =
            new SimpleGrantedAuthority("CAN_AUTO_APPROVED_MATERIAL");

    public MaterialCreateResponse create(MaterialCreateRequest body) {
        var userPrincipal = AuthFacade.getCurrentUserPrincipal()
                .orElseThrow(ServerProcessingException::new);
        var category = categoryRepositoryHandler.findById(body.categoryId)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
        var type = typeRepositoryHandler.findById(body.typeId)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
        var tags = tagRepositoryHandler.findAllByIdIn(body.tagsIds)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);

        return Try.of(() -> this.toMaterial(body, userPrincipal, category, type, tags))
                .flatMap(materialRepositoryHandler::save)
                .peek(material -> {
                    if (material.getStatus() == APPROVED)
                        materialProducer.sendNewMaterialToIndex(MaterialRabbitProducer.Message.map(material, 0.00));
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
        var isApprovedMaterial = userPrincipal.getAuthorities().contains(CAN_AUTO_APPROVED_MATERIAL)
                ? APPROVED : PENDING;
        material.setStatus(isApprovedMaterial);
        return material;
    }

    @Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
    public interface MaterialCreateMapper {
        MaterialCreateResponse map(Material material);
    }

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
