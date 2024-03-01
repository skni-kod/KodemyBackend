package pl.sknikod.kodemybackend.infrastructure.material;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.configuration.RabbitConfig;
import pl.sknikod.kodemybackend.configuration.SecurityConfig;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.infrastructure.common.EntityDao;
import pl.sknikod.kodemybackend.infrastructure.common.entity.*;
import pl.sknikod.kodemybackend.infrastructure.common.repository.AuthorRepository;
import pl.sknikod.kodemybackend.infrastructure.common.repository.GradeRepository;
import pl.sknikod.kodemybackend.infrastructure.common.repository.MaterialRepository;
import pl.sknikod.kodemybackend.infrastructure.material.rest.MaterialCreateRequest;
import pl.sknikod.kodemybackend.infrastructure.material.rest.MaterialCreateResponse;

import java.util.Set;

import static pl.sknikod.kodemybackend.infrastructure.common.entity.Material.MaterialStatus.APPROVED;
import static pl.sknikod.kodemybackend.infrastructure.common.entity.Material.MaterialStatus.PENDING;

@Component
@AllArgsConstructor
@Slf4j
public class MaterialCreateUseCase {

    private final MaterialRepository materialRepository;
    private final MaterialCreateMapper createMaterialMapper;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitConfig.QueueProperties queueProperties;
    private final MaterialRabbitMapper rabbitMapper;
    private final GradeRepository gradeRepository;
    private final EntityDao entityDao;
    private final AuthorRepository authorRepository;

    public MaterialCreateResponse execute(MaterialCreateRequest body) {
        var userPrincipal = getUserPrincipal();
        return Option.of(body)
                .map(materialCreateRequest -> createMaterialMapper.map(
                        body,
                        authorRepository.findById(userPrincipal.getId())
                                .orElseGet(() -> authorRepository.save(Author.map(userPrincipal))),
                        entityDao.findCategoryById(body.getCategoryId()),
                        entityDao.findTypeById(body.getTypeId()),
                        entityDao.findTechnologySetByIds(body.getTechnologiesIds())
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
}
