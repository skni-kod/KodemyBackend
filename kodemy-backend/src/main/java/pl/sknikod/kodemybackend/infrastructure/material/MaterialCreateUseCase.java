package pl.sknikod.kodemybackend.infrastructure.material;

import io.swagger.v3.oas.annotations.media.Schema;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.configuration.SecurityConfig;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Author;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.common.repository.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.List;

import static pl.sknikod.kodemybackend.infrastructure.common.entity.Material.MaterialStatus.APPROVED;
import static pl.sknikod.kodemybackend.infrastructure.common.entity.Material.MaterialStatus.PENDING;

@Component
@AllArgsConstructor
@Slf4j
public class MaterialCreateUseCase {
    private final GradeRepository gradeRepository;
    private final TechnologyRepository technologyRepository;
    private final TypeRepository typeRepository;
    private final CategoryRepository categoryRepository;
    private final MaterialRepository materialRepository;
    private final MaterialCreateMapper createMaterialMapper;
    private final AuthorRepository authorRepository;
    private final MaterialOSearchUseCase materialOSearchUseCase;

    public MaterialCreateResponse create(MaterialCreateRequest body) {
        return Option.of(this.map(body))
                .map(materialRepository::save)
                .peek(material -> {
                    if (material.getStatus() == APPROVED)
                        this.materialOSearchUseCase.index(material);
                })
                .map(createMaterialMapper::map)
                .getOrElseThrow(() -> new ServerProcessingException(
                        ServerProcessingException.Format.PROCESS_FAILED, Material.class
                ));
    }

    private Material map(MaterialCreateRequest body) {
        var userPrincipal = getUserPrincipal();
        var material = new Material();
        var author = authorRepository.findById(userPrincipal.getId())
                .orElseGet(() -> Try.of(() -> authorRepository.save(
                                new Author(userPrincipal.getId(), userPrincipal.getUsername())
                        ))
                        .getOrElseThrow(() -> new ServerProcessingException(
                                ServerProcessingException.Format.PROCESS_FAILED, Author.class
                        )));
        material.setAuthor(author);
        material.setCategory(categoryRepository.findCategoryById(body.getCategoryId()));
        material.setType(typeRepository.findTypeById(body.getTypeId()));
        material.setTechnologies(technologyRepository.findTechnologiesByIdIn(body.getTechnologiesIds()));
        var isApprovedMaterial = getUserPrincipal().getAuthorities()
                .contains(new SimpleGrantedAuthority("CAN_AUTO_APPROVED_MATERIAL"));
        material.setStatus(isApprovedMaterial ? APPROVED : PENDING);
        material.setActive(true);
        material.setTitle(body.getTitle());
        material.setDescription(body.getDescription());
        material.setLink(body.getLink());
        return material;
    }

    private static SecurityConfig.UserPrincipal getUserPrincipal() {
        return (SecurityConfig.UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    @Mapper(componentModel = "spring")
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

        private List<@NotNull @Positive(message = "Technology ID must be > 0") Long> technologiesIds;
    }

    @Value
    public static class MaterialCreateResponse {
        Long id;
        String title;
        @Enumerated(EnumType.STRING)
        Material.MaterialStatus status;
    }
}
