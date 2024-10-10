package pl.sknikod.kodemybackend.infrastructure.module.grade;

import io.vavr.control.Try;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.GradeMapper;
import pl.sknikod.kodemybackend.infrastructure.common.model.UserDetails;
import pl.sknikod.kodemybackend.infrastructure.database.Grade;
import pl.sknikod.kodemybackend.infrastructure.database.Material;
import pl.sknikod.kodemybackend.infrastructure.dao.GradeDao;
import pl.sknikod.kodemybackend.infrastructure.dao.MaterialDao;
import pl.sknikod.kodemybackend.infrastructure.database.GradeRepository;
import pl.sknikod.kodemybackend.infrastructure.module.grade.model.GradeMaterialSearchFields;
import pl.sknikod.kodemycommon.exception.InternalError500Exception;
import pl.sknikod.kodemycommon.exception.content.ExceptionUtil;
import pl.sknikod.kodemycommon.security.AuthFacade;
import pl.sknikod.kodemycommon.security.UserPrincipal;

import java.util.Date;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class MaterialGradeService {
    private final MaterialDao materialDao;
    private final GradeMapper gradeMapper;
    private final GradeDao gradeDao;

    public Grade addGrade(Long materialId, MaterialAddGradeRequest request) {
        var userPrincipal = AuthFacade.getCurrentUserPrincipal()
                .orElseThrow(InternalError500Exception::new);
        var material = materialDao.findById(materialId)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
        return Try.of(() -> this.map(request, material, userPrincipal))
                .flatMap(gradeDao::save)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    private Grade map(MaterialAddGradeRequest request, Material material, UserPrincipal userPrincipal) {
        var grade = new Grade();
        grade.setMaterial(material);
        grade.setUserId(userPrincipal.getId());
        grade.setValue(Double.parseDouble(request.getGrade()));
        return grade;
    }

    public Page<GradePageable> showGrades(PageRequest pageRequest, GradeMaterialSearchFields searchFields, Long materialId) {
        Date minDate = Objects.requireNonNullElse(
                searchFields.getCreatedDateFrom(), GradeRepository.DATE_MIN);
        Date maxDate = Objects.requireNonNullElse(
                searchFields.getCreatedDateTo(), GradeRepository.DATE_MAX);
        return gradeDao.findGradesByMaterialInDateRange(materialId, minDate, maxDate, pageRequest)
                .map(page -> {
                    var list = page.getContent().stream().map(gradeMapper::map).toList();
                    return new PageImpl<>(list, page.getPageable(), page.getTotalElements());
                })
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    @Data
    public static class MaterialAddGradeRequest {
        @NotNull
        @Pattern(regexp = "^[0-5](\\.[05])?$")
        private String grade;
    }

    public record GradePageable(Long id, Double value, UserDetails author) {
    }
}

