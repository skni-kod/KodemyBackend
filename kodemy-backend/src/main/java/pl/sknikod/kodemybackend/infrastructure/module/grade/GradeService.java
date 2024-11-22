package pl.sknikod.kodemybackend.infrastructure.module.grade;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemybackend.infrastructure.database.Grade;
import pl.sknikod.kodemybackend.infrastructure.database.GradeRepository;
import pl.sknikod.kodemybackend.infrastructure.mapper.GradeMapper;
import pl.sknikod.kodemybackend.infrastructure.module.grade.model.GradeMaterialFilterSearchParams;
import pl.sknikod.kodemybackend.infrastructure.store.GradeStore;
import pl.sknikod.kodemybackend.infrastructure.store.UserStore;
import pl.sknikod.kodemycommons.exception.content.ExceptionUtil;

import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GradeService {
    private final GradeMapper gradeMapper;
    private final GradeStore gradeStore;

    public Grade addGrade(Long materialId, MaterialAddGradeRequest request) {
        return gradeStore.addGrade(materialId, Double.parseDouble(request.getGrade()))
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    public Page<GradePageable> showGrades(PageRequest pageRequest, GradeMaterialFilterSearchParams filterSearchParams, Long materialId) {
        Date minDate = Objects.requireNonNullElse(
                filterSearchParams.getCreatedDateFrom(), GradeRepository.DATE_MIN);
        Date maxDate = Objects.requireNonNullElse(
                filterSearchParams.getCreatedDateTo(), GradeRepository.DATE_MAX);
        return gradeStore.findGradesByMaterialInDateRange(materialId, minDate, maxDate, pageRequest)
                .map(tuple -> tuple.map2(users -> {
                    return users.stream().collect(Collectors.toMap(UserStore.User::getId, UserStore.User::getUsername));
                }))
                .mapTry(tuple -> tuple._1.map(grade -> gradeMapper.map(grade, tuple._2.get(grade.getUserId()))))
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    @Data
    public static class MaterialAddGradeRequest {
        @NotNull
        @Pattern(regexp = "^[0-5](\\.[05])?$")
        private String grade;
    }

    public record GradePageable(Long id, Double value, AuthorDetails author) {
        public record AuthorDetails(Long id, String username) {
        }
    }
}

