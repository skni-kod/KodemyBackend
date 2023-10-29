package pl.sknikod.kodemybackend.infrastructure.material;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.configuration.RabbitConfig;
import pl.sknikod.kodemybackend.configuration.SecurityConfig;
import pl.sknikod.kodemybackend.exception.structure.NotFoundException;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.infrastructure.common.entity.*;
import pl.sknikod.kodemybackend.infrastructure.common.repository.*;
import pl.sknikod.kodemybackend.infrastructure.material.rest.MaterialCreateRequest;
import pl.sknikod.kodemybackend.infrastructure.material.rest.MaterialCreateResponse;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class MaterialCreateUseCase {

    private final MaterialRepository materialRepository;
    private final MaterialCreateMapper createMaterialMapper;
    private final CategoryRepository categoryRepository;
    private final TypeRepository typeRepository;
    private final TechnologyRepository technologyRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitConfig.QueueProperties queueProperties;
    private final MaterialRabbitMapper rabbitMapper;
    private final GradeRepository gradeRepository;
    private final ObjectMapper objectMapper;

    public MaterialCreateResponse execute(MaterialCreateRequest body) {
        return Option.of(body)
                .map(createMaterialMapper::map)
                .map(material -> initializeMissingMaterialProperties(body, material))
                .map(materialRepository::save)
                .peek(this::executeOpenSearchIndex)
                .peek(this::executeNotificationStatus)
                .map(createMaterialMapper::map)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Material.class));
    }

    private Material initializeMissingMaterialProperties(MaterialCreateRequest body, Material material) {
        var principal = (SecurityConfig.JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        material.setAuthor(new UserJsonB(principal.getId(), principal.getUsername()));
        material.setCategory(categoryRepository.findById(body.getCategoryId()).orElseThrow(() ->
                new NotFoundException(NotFoundException.Format.ENTITY_ID, Category.class, body.getCategoryId())
        ));
        material.setTechnologies(fetchTechnologies(body.getTechnologiesIds()));
        material.setType(typeRepository.findById(body.getTypeId()).orElseThrow(() ->
                new NotFoundException(NotFoundException.Format.ENTITY_ID, Type.class, body.getTypeId())
        ));
        return material;
    }

    private Set<Technology> fetchTechnologies(Set<Long> technologiesIds) {
        return technologiesIds
                .stream()
                .map(id -> technologyRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(NotFoundException.Format.ENTITY_ID, Technology.class, id))
                )
                .collect(Collectors.toSet());
    }

    private void executeNotificationStatus(Material material) {
        Option.of(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof SecurityConfig.JwtUserDetails)
                .map(principal -> (SecurityConfig.JwtUserDetails) principal);
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
}
