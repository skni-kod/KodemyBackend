package pl.sknikod.kodemybackend.infrastructure.database.handler;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Grade;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.database.repository.GradeRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class GradeRepositoryHandler {
    private final GradeRepository gradeRepository;

    public Try<Double> findAvgGradeByMaterial(Long id) {
        return Try.of(() -> gradeRepository.findAvgGradeByMaterialId(id));
    }

    public Try<List<Long>> getGradeStats(Long materialId) {
        return Try.of(() -> Stream
                .iterate(1.0, i -> i <= 5.0, i -> i + 1.0)
                .map(i -> gradeRepository.countAllByMaterialIdAndValue(materialId, i))
                .toList());
    }

    public Try<Grade> save(Grade grade) {
        return Try.of(() -> gradeRepository.save(grade))
                .onFailure(th -> log.error("Cannot save grade", th));
    }

    public Try<Page<Grade>> findGradesByMaterialInDateRange(
            Long materialId,
            Date minDate,
            Date maxDate,
            PageRequest pageRequest
    ) {
        return Try.of(() -> gradeRepository.findGradesByMaterialInDateRange(
                materialId,
                minDate, maxDate,
                PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort())
        ));
    }
}
