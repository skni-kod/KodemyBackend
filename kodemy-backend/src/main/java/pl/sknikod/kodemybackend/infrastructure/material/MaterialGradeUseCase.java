package pl.sknikod.kodemybackend.infrastructure.material;

import io.vavr.control.Option;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.exception.structure.ValidationException;
import pl.sknikod.kodemybackend.util.ContextUtil;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Grade;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.GradeMapper;
import pl.sknikod.kodemybackend.infrastructure.common.repository.AuthorRepository;
import pl.sknikod.kodemybackend.infrastructure.common.repository.GradeRepository;
import pl.sknikod.kodemybackend.infrastructure.common.repository.MaterialRepository;
import pl.sknikod.kodemybackend.infrastructure.common.rest.UserDetails;
import pl.sknikod.kodemybackend.infrastructure.material.rest.GradeMaterialSearchFields;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class MaterialGradeUseCase {
    private final AuthorRepository authorRepository;
    private final MaterialRepository materialRepository;
    private final GradeMapper gradeMapper;
    private final GradeRepository gradeRepository;

    public void addGrade(Long materialId, MaterialAddGradeRequest body) {
        Option.of(this.map(materialId, body))
                .map(gradeRepository::save)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Material.class));
    }

    public Page<GradePageable> showGrades(PageRequest pageRequest, GradeMaterialSearchFields searchFields, Long materialId) {
        Date minDate = Objects.nonNull(searchFields.getCreatedDateFrom())
                ? searchFields.getCreatedDateFrom() : GradeRepository.DATE_MIN;
        Date maxDate = Objects.nonNull(searchFields.getCreatedDateTo())
                ? searchFields.getCreatedDateTo() : GradeRepository.DATE_MAX;
        Page<Grade> gradesPage = gradeRepository.findGradesByMaterialInDateRange(
                materialId,
                minDate, maxDate,
                PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort())
        );
        List<GradePageable> singleGradeResponses = gradesPage.getContent()
                .stream()
                .map(gradeMapper::map)
                .toList();
        return new PageImpl<>(singleGradeResponses, gradesPage.getPageable(), gradesPage.getTotalElements());
    }

    private Grade map(Long materialId, MaterialAddGradeRequest request) {
        var userPrincipal = Option.ofOptional(ContextUtil.getCurrentUserPrincipal())
                .getOrElseThrow(() -> new ValidationException("User not authorized"));
        var grade = new Grade();
        grade.setMaterial(materialRepository.findMaterialById(materialId));
        var authorOptional = authorRepository.findById(userPrincipal.getId());
        if (authorOptional.isEmpty())
            throw new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Material.class);
        grade.setAuthor(authorOptional.get());
        grade.setValue(Double.parseDouble(request.getGrade()));
        return grade;
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
