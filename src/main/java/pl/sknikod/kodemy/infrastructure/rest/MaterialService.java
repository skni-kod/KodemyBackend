package pl.sknikod.kodemy.infrastructure.rest;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.exception.structure.NotFoundException;
import pl.sknikod.kodemy.exception.structure.ServerProcessingException;
import pl.sknikod.kodemy.infrastructure.model.entity.*;
import pl.sknikod.kodemy.infrastructure.model.repository.*;
import pl.sknikod.kodemy.infrastructure.rest.mapper.GradeMapper;
import pl.sknikod.kodemy.infrastructure.rest.mapper.MaterialCreateMapper;
import pl.sknikod.kodemy.infrastructure.rest.model.MaterialAddGradeRequest;
import pl.sknikod.kodemy.infrastructure.rest.model.MaterialCreateRequest;
import pl.sknikod.kodemy.infrastructure.rest.model.MaterialCreateResponse;
import pl.sknikod.kodemy.infrastructure.rest.model.SingleGradeResponse;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MaterialService {
    private final MaterialRepository materialRepository;
    private final MaterialCreateMapper createMaterialMapper;
    private final NotificationService notificationService;
    private final GradeMapper gradeMapper;
    private final OpenSearchService openSearchService;
    private final GradeRepository gradeRepository;
    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final TechnologyRepository technologyRepository;
    private final TypeRepository typeRepository;

    public MaterialCreateResponse create(MaterialCreateRequest body) {
        return Option.of(body)
                .map(createMaterialMapper::map)
                .peek(material -> {
                    material.setUser(userService.getContextUser());
                    material.setCategory(categoryRepository.findById(body.getCategoryId()).orElseThrow(() ->
                            new NotFoundException(NotFoundException.Format.ENTITY_ID, Category.class, body.getCategoryId())
                    ));
                    material.setTechnologies(processTechnologies(body.getTechnologiesIds()));
                    material.setType(typeRepository.findById(body.getTypeId()).orElseThrow(() ->
                            new NotFoundException(NotFoundException.Format.ENTITY_ID, Type.class, body.getTypeId())
                    ));
                })
                .map(materialRepository::save)
                .peek(material -> {
                    openSearchService.createIndexIfNotExists(OpenSearchService.Info.MATERIAL);
                    openSearchService.indexMaterial(material);
                })
                .map(this::checkApproval)
                .map(createMaterialMapper::map)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Material.class));
    }

    private Set<Technology> processTechnologies(Set<Long> technologiesIds) {
        return technologiesIds
                .stream()
                .map(id -> technologyRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(NotFoundException.Format.ENTITY_ID, Technology.class, id))
                )
                .collect(Collectors.toSet());
    }

    private Material checkApproval(Material material) {
        if (!UserService.checkPrivilege("CAN_AUTO_APPROVED_MATERIAL"))
            notificationService.sendNotificationToAdmins(
                    NotificationTitle.MATERIAL_APPROVAL_REQUEST.getDesc(),
                    String.format("{\"id\":%d, \"title\":\"%s\"}", material.getId(), material.getTitle())
            );
        return material;
    }

    public void addGrade(MaterialAddGradeRequest body, Long materialId) {
        Material material = materialRepository.findById(materialId).orElseThrow(() ->
                new NotFoundException(NotFoundException.Format.ENTITY_ID, Material.class, materialId)
        );
        Option.of(body)
                .map(gradeMapper::map)
                .peek(grade -> {
                    grade.setUser(userService.getContextUser());
                    grade.setMaterial(material);
                })
                .map(gradeRepository::save)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Material.class));
    }

    public Set<SingleGradeResponse> showGrades(Long materialId) {
        return Option.of(materialRepository.findById(materialId).orElseThrow(() ->
                        new NotFoundException(NotFoundException.Format.ENTITY_ID, Material.class, materialId))
                )
                .map(Material::getGrades)
                .map(gradeMapper::map)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Material.class));
    }
}