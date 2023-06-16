package pl.sknikod.kodemy.infrastructure.rest;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.exception.structure.NotFoundException;
import pl.sknikod.kodemy.exception.structure.ServerProcessingException;
import pl.sknikod.kodemy.infrastructure.model.grade.Grade;
import pl.sknikod.kodemy.infrastructure.model.material.Material;
import pl.sknikod.kodemy.infrastructure.model.material.MaterialRepository;
import pl.sknikod.kodemy.infrastructure.model.notification.NotificationTitle;
import pl.sknikod.kodemy.infrastructure.model.user.UserPrincipal;
import pl.sknikod.kodemy.infrastructure.rest.mapper.GradeMapper;
import pl.sknikod.kodemy.infrastructure.rest.mapper.MaterialCreateMapper;
import pl.sknikod.kodemy.infrastructure.rest.model.MaterialAddGradeRequest;
import pl.sknikod.kodemy.infrastructure.rest.model.MaterialCreateRequest;
import pl.sknikod.kodemy.infrastructure.rest.model.MaterialCreateResponse;
import pl.sknikod.kodemy.infrastructure.rest.model.SingleGradeResponse;

import java.util.Set;

@Service
@AllArgsConstructor
public class MaterialService {
    private final MaterialRepository materialRepository;
    private final MaterialCreateMapper createMaterialMapper;
    private final NotificationService notificationService;
    private final GradeMapper gradeMapper;
    private final RestHighLevelClient restHighLevelClient;
    private final RequestOptions requestOptions;
    private final OpenSearchService openSearchService;

    public MaterialCreateResponse create(MaterialCreateRequest body) {
        return Option.of(body)
                .map(createMaterialMapper::map)
                .map(materialRepository::save)
                .peek(material -> {
                    openSearchService.createIndexIfNotExists(OpenSearchService.Info.MATERIAL);
                    openSearchService.indexMaterial(material);
                })
                .map(this::checkApproval)
                .map(createMaterialMapper::map)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.processFailed, Material.class));
    }

    public void addGrade(MaterialAddGradeRequest body, Long materialId) {
        Material material = Option.ofOptional(materialRepository.findById(materialId))
                .getOrElseThrow(() -> new NotFoundException(NotFoundException.Format.entityId, Material.class, materialId));
        Grade grade = Option.of(body)
                .map(gradeMapper::map)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.processFailed, Material.class));

        grade.setMaterial(material);
    }

    private Material checkApproval(Material material) {
        if (!UserPrincipal.checkPrivilege("CAN_AUTO_APPROVED_MATERIAL"))
            notificationService.sendNotificationToAdmins(
                    NotificationTitle.MATERIAL_APPROVAL_REQUEST.getDesc(),
                    String.format("{\"id\":%d, \"title\":\"%s\"}", material.getId(), material.getTitle())
            );
        return material;
    }

    public Set<SingleGradeResponse> showGrades(Long materialId) {
        return Option.of(Option.
                        ofOptional(materialRepository.findById(materialId))
                        .getOrElseThrow(() -> new NotFoundException(NotFoundException.Format.entityId, Material.class, materialId)))
                .map(Material::getGrades)
                .map(gradeMapper::map)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.processFailed, Material.class));
    }
}