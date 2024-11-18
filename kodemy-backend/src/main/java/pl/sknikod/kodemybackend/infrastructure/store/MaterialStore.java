package pl.sknikod.kodemybackend.infrastructure.store;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.infrastructure.aspect.AfterAction;
import pl.sknikod.kodemybackend.infrastructure.database.Material;
import pl.sknikod.kodemybackend.infrastructure.database.MaterialRepository;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.FilterSearchParams;
import pl.sknikod.kodemycommons.exception.NotFound404Exception;
import pl.sknikod.kodemycommons.exception.content.ExceptionMsgPattern;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MaterialStore {
    private final MaterialRepository materialRepository;
    private final UserStore userStore;
    private final GradeStore gradeStore;

    public Try<FindByIdObject> findById(Long id, boolean isOnlyMaterial) {
        return Try.of(() -> materialRepository.findById(id).orElseThrow(() -> new NotFound404Exception(
                        ExceptionMsgPattern.ENTITY_NOT_FOUND_BY_PARAM, Material.class.getSimpleName(), "id", id
                )))
                .mapTry(material -> {
                    if (isOnlyMaterial) {
                        return new FindByIdObject(material, null, null, null);
                    }
                    var user = userStore.findById(material.getId()).get();
                    var avgGrade = gradeStore.findAvgGradeByMaterial(material.getId());
                    var gradeStats = gradeStore.getGradeStats(material.getId());
                    return new FindByIdObject(material, user.getUsername(), avgGrade.get(), gradeStats.get());
                })
                .onFailure(th -> log.error(th.getMessage(), th));
    }

    public Try<FindByIdObject> findById(Long id) {
        return findById(id, false);
    }

    @AfterAction(action = AfterAction.Action.SAVE)
    public Try<Material> save(Material material) {
        return Try.of(() -> materialRepository.save(material))
                .onFailure(th -> log.error("Cannot save material", th));
    }

    @AfterAction(action = AfterAction.Action.UPDATE)
    public Try<Material> update(Material material) {
        return Try.of(() -> materialRepository.save(material))
                .onFailure(th -> log.error("Cannot update material", th));
    }

    public Try<Tuple2<Page<FindAllPageObject>, UserStore.User>> findAll(
            FilterSearchParams filterSearchParams,
            List<Material.MaterialStatus> statuses,
            Long userId,
            PageRequest pageRequest
    ) {
        return userStore.findById(userId).mapTry(user -> {
            var materials = materialRepository.searchMaterialsWithAvgGrades(
                    filterSearchParams.getId(),
                    filterSearchParams.getPhrase(),
                    statuses,
                    filterSearchParams.getCreatedBy(),
                    filterSearchParams.getSectionId(),
                    filterSearchParams.getCategoryIds(),
                    filterSearchParams.getTagIds(),
                    userId,
                    filterSearchParams.getCreatedDateFrom(),
                    filterSearchParams.getCreatedDateTo(),
                    filterSearchParams.getMinAvgGrade(),
                    filterSearchParams.getMaxAvgGrade(),
                    pageRequest
            );
            return Tuple.of(materials.map(FindAllPageObject::new), user);
        });
    }

    @AfterAction(action = AfterAction.Action.STATUS_UPDATE)
    public Try<Tuple2<Long, Material.MaterialStatus>> changeStatus(Long materialId, Material.MaterialStatus newStatus) {
        return Try.of(() -> {
                    materialRepository.updateStatus(materialId, newStatus);
                    return Tuple.of(materialId, newStatus);
                })
                .onFailure(th -> log.error("Cannot change material status to {}", newStatus, th));
    }

    @Value
    public static class FindByIdObject {
        Material material;
        String username;
        Double avgGrade;
        List<Long> gradeStats;
    }

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class FindAllPageObject {
        Material material;
        Double avgGrade;

        public FindAllPageObject(Object[] objects) {
            this.material = (Material) objects[0];
            this.avgGrade = (Double) objects[1];
        }
    }
}
