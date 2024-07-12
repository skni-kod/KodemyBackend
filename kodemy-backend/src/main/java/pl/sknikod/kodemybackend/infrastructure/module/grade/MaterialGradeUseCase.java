package pl.sknikod.kodemybackend.infrastructure.module.grade;

import io.vavr.Tuple;
import io.vavr.control.Option;
import io.vavr.control.Try;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import pl.sknikod.kodemybackend.exception.ExceptionUtil;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.GradeMapper;
import pl.sknikod.kodemybackend.infrastructure.common.model.UserDetails;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Grade;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.database.handler.GradeRepositoryHandler;
import pl.sknikod.kodemybackend.infrastructure.database.handler.MaterialRepositoryHandler;
import pl.sknikod.kodemybackend.infrastructure.database.repository.GradeRepository;
import pl.sknikod.kodemybackend.infrastructure.module.grade.model.GradeMaterialSearchFields;
import pl.sknikod.kodemybackend.util.auth.AuthFacade;
import pl.sknikod.kodemybackend.util.auth.UserPrincipal;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class MaterialGradeUseCase {
    private final MaterialRepositoryHandler materialRepositoryHandler;
    private final GradeMapper gradeMapper;
    private final GradeRepositoryHandler gradeRepositoryHandler;

    public Grade addGrade(Long materialId, MaterialAddGradeRequest request) {
        var userPrincipal = AuthFacade.getCurrentUserPrincipal()
                .orElseThrow(ServerProcessingException::new);
        var material = materialRepositoryHandler.findById(materialId)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
        return Try.of(() -> this.map(request, material, userPrincipal))
                .flatMap(gradeRepositoryHandler::save)
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
        return gradeRepositoryHandler.findGradesByMaterialInDateRange(materialId, minDate, maxDate, pageRequest)
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

