package pl.sknikod.kodemybackend.infrastructure.store;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.infrastructure.aspect.AfterAction;
import pl.sknikod.kodemybackend.infrastructure.database.Grade;
import pl.sknikod.kodemybackend.infrastructure.database.GradeRepository;
import pl.sknikod.kodemybackend.infrastructure.database.Material;
import pl.sknikod.kodemybackend.infrastructure.database.MaterialRepository;
import pl.sknikod.kodemycommons.exception.NotFound404Exception;
import pl.sknikod.kodemycommons.exception.content.ExceptionMsgPattern;
import pl.sknikod.kodemycommons.security.AuthFacade;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class GradeStore {
    private final GradeRepository gradeRepository;
    private final UserStore userStore;
    private final MaterialRepository materialRepository;

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

    public Try<Tuple2<Page<Grade>, HashSet<UserStore.User>>> findGradesByMaterialInDateRange(
            Long materialId,
            Date minDate,
            Date maxDate,
            PageRequest pageRequest
    ) {
        LocalDateTime fromDateTime = minDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime toDateTime = maxDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        return Try.of(() -> gradeRepository.findGradesByMaterialInDateRange(
                        materialId,
                        fromDateTime, toDateTime,
                        PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort())
                ))
                .mapTry(grades -> {
                    if (grades.getTotalElements() == 0){
                        return Tuple.of(grades, new HashSet<>());
                    }

                    var usersId = grades.stream().map(Grade::getUserId).collect(Collectors.toSet());
                    var users = userStore.findUsersById(usersId)
                            .map(HashSet::new)
                            .getOrElseThrow(() -> new IllegalStateException("Failed to retrieve users by ID."));

                    if (users.size() != usersId.size()) {
                        throw new IllegalStateException("Number of users does not match");
                    }

                    return Tuple.of(grades, users);
                });
    }

    @AfterAction(action = AfterAction.Action.GRADE_ADD)
    public Try<Grade> addGrade(Long materialId, double value) {
        return Try.of(() -> {
                    if (materialRepository.existsById(materialId)) {
                        return true;
                    }
                    throw new NotFound404Exception(
                            ExceptionMsgPattern.ENTITY_NOT_FOUND_BY_PARAM, Material.class.getSimpleName(), "id", materialId
                    );
                })
                .mapTry(unused -> new Grade(value, AuthFacade.getCurrentUserPrincipal().get().getId(), materialId))
                .map(gradeRepository::save)
                .onFailure(th -> log.error("Cannot add grade", th));
    }

    public Try<List<FindAvgGradeObject>> findAvgGradeByMaterialsIds(Collection<Long> ids) {
        return Try.of(() -> gradeRepository.findAvgGradeByMaterialsIds(ids))
                .map(Collection::stream)
                .map(objects -> objects.map(FindAvgGradeObject::new).toList());
    }

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class FindAvgGradeObject {
        Long materialId;
        Double avgGrade;

        public FindAvgGradeObject(Object[] objects) {
            this.materialId = (Long) objects[0];
            this.avgGrade = (Double) objects[1];
        }
    }
}
