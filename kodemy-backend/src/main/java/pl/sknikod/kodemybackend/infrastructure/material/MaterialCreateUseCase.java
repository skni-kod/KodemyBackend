package pl.sknikod.kodemybackend.infrastructure.material;

import io.swagger.v3.oas.annotations.media.Schema;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.configuration.RabbitConfig;
import pl.sknikod.kodemybackend.configuration.SecurityConfig;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.infrastructure.common.entity.*;
import pl.sknikod.kodemybackend.infrastructure.common.repository.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.Set;

import static pl.sknikod.kodemybackend.infrastructure.common.entity.Material.MaterialStatus.APPROVED;
import static pl.sknikod.kodemybackend.infrastructure.common.entity.Material.MaterialStatus.PENDING;

@Component
@AllArgsConstructor
@Slf4j
public class MaterialCreateUseCase {
    private final TechnologyRepository technologyRepository;
    private final TypeRepository typeRepository;
    private final CategoryRepository categoryRepository;

    private final MaterialRepository materialRepository;
    private final MaterialCreateMapper createMaterialMapper;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitConfig.QueueProperties queueProperties;
    private final MaterialRabbitMapper rabbitMapper;
    private final GradeRepository gradeRepository;
    private final AuthorRepository authorRepository;

    public MaterialCreateResponse create(MaterialCreateRequest body) {
        var userPrincipal = getUserPrincipal();
        return Option.of(body)
                .map(materialCreateRequest -> createMaterialMapper.map(
                        body,
                        authorRepository.findById(userPrincipal.getId())
                                .orElseGet(() -> authorRepository.save(Author.map(userPrincipal))),
                        categoryRepository.findCategoryById(body.getCategoryId()),
                        typeRepository.findTypeById(body.getTypeId()),
                        technologyRepository.findTechnologySetByIds(body.getTechnologiesIds())
                ))
                .map(this::updateStatus)
                .map(materialRepository::save)
                .peek(this::executeOpenSearchIndex)
                .peek(this::executeNotificationStatus)
                .map(createMaterialMapper::map)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Material.class));
    }

    private static SecurityConfig.UserPrincipal getUserPrincipal() {
        return (SecurityConfig.UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }


    private Material updateStatus(Material material) {
        material.setStatus(
                getUserPrincipal().getAuthorities()
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
                queueProperties.get("m-created").getName(),
                "",
                rabbitMapper.map(
                        material,
                        gradeRepository.findAverageGradeByMaterialId(material.getId())
                )
        );
    }

    @Mapper(componentModel = "spring")
    public interface MaterialCreateMapper {
        default Material map(
                MaterialCreateRequest body,
                Author author,
                Category category,
                Type type,
                Set<Technology> technologies
        ) {
            var material = new Material();
            material.setActive(true);
            material.setStatus(PENDING);
            material.setTitle(body.getTitle());
            material.setDescription(body.getDescription());
            material.setLink(body.getLink());
            material.setCategory(category);
            material.setType(type);
            material.setTechnologies(technologies);
            material.setAuthor(author);
            return material;
        }

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

        private Set<@NotNull @Positive(message = "Technology ID must be > 0") Long> technologiesIds;
    }

    @Value
    public static class MaterialCreateResponse {
        Long id;
        String title;
        @Enumerated(EnumType.STRING)
        Material.MaterialStatus status;
    }
}
