package pl.sknikod.kodemy.material;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.exception.general.GeneralRuntimeException;
import pl.sknikod.kodemy.exception.general.NotFoundException;
import pl.sknikod.kodemy.grade.Grade;
import pl.sknikod.kodemy.grade.GradeRepository;
import pl.sknikod.kodemy.notification.NotificationService;
import pl.sknikod.kodemy.notification.NotificationTitle;
import pl.sknikod.kodemy.rest.mapper.GradeMapper;
import pl.sknikod.kodemy.rest.mapper.MaterialMapper;
import pl.sknikod.kodemy.rest.request.MaterialCreateRequest;
import pl.sknikod.kodemy.rest.request.MaterialAddGradeRequest;
import pl.sknikod.kodemy.rest.response.MaterialCreateResponse;
import pl.sknikod.kodemy.rest.response.SingleGradeResponse;
import pl.sknikod.kodemy.user.UserPrincipal;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MaterialService {
    private final MaterialRepository materialRepository;
    private final MaterialMapper materialMapper;
    private final NotificationService notificationService;
    private final GradeMapper gradeMapper;
    private final GradeRepository gradeRepository;

    public MaterialCreateResponse create(MaterialCreateRequest body) {
        return Option.of(body)
                .map(materialMapper::map)
                .map(materialRepository::save)
                .map(this::checkApproval)
                .map(materialMapper::map)
                .getOrElseThrow(() -> new GeneralRuntimeException("Failed to processing"));
    }

    public void addGrade(MaterialAddGradeRequest body, Long materialId) {
        Material material = Option.ofOptional(materialRepository.findById(materialId))
                .getOrElseThrow(() -> new GeneralRuntimeException("Not Found"));
        Grade grade = Option.of(body)
                .map(gradeMapper::map)
                .getOrElseThrow(() -> new GeneralRuntimeException("Failed to processing"));

        grade.setMaterial(material);
    }

    private Material checkApproval(Material material) {
        if (!UserPrincipal.checkPrivilege("CAN_AUTO_APPROVED_MATERIAL"))
            notificationService.sendNotificationToAdmins(
                    NotificationTitle.MATERIAL_APPROVAL_REQUEST.getDesc(),
                    material.getId().toString()
            );
        return material;
    }

    public Set<SingleGradeResponse> showGrades(Long materialId) {
        return Option.of(Option.
                        ofOptional(materialRepository.findById(materialId))
                        .getOrElseThrow(() -> new NotFoundException("Material Not Found")))
                .map(Material::getGrades)
                .map(gradeMapper::map)
                .getOrElseThrow(() -> new GeneralRuntimeException("Failed to processing"));
    }
}