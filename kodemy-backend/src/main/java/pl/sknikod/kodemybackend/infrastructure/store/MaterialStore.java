package pl.sknikod.kodemybackend.infrastructure.store;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.infrastructure.aspect.AfterAction;
import pl.sknikod.kodemybackend.infrastructure.database.Material;
import pl.sknikod.kodemybackend.infrastructure.database.MaterialRepository;
import pl.sknikod.kodemycommons.exception.NotFound404Exception;
import pl.sknikod.kodemycommons.exception.content.ExceptionMsgPattern;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
                    var user = userStore.findById(material.getUserId()).get();
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

    public Try<Tuple2<Page<FindAllPageObject>, UserStore.User>> findAll(Long userId, FindAllFilters filters) {
        return Try.of(() -> materialRepository.searchMaterialsWithAvgGrades(
                        filters.getId(),
                        filters.getPhrase(),
                        filters.statuses,
                        filters.getSectionId(),
                        filters.getCategoryIds(),
                        filters.getTagIds(),
                        userId,
                        null,
                        null,
                        filters.getMinAvgGrade(),
                        filters.getMaxAvgGrade(),
                        filters.pageRequest
                ))
                .mapTry(materials -> Tuple.of(
                        materials.map(FindAllPageObject::new),
                        materials.getTotalElements() == 0 ? new UserStore.User() : userStore.findById(userId).get()
                ));
    }

    public Try<Tuple2<Page<FindAllPageObject>, HashSet<UserStore.User>>> findAll(FindAllFilters filters) {
        return Try.of(() -> materialRepository.searchMaterialsWithAvgGrades(
                        filters.getId(),
                        filters.getPhrase(),
                        filters.statuses,
                        filters.getSectionId(),
                        filters.getCategoryIds(),
                        filters.getTagIds(),
                        null,
                        null,
                        null,
                        filters.getMinAvgGrade(),
                        filters.getMaxAvgGrade(),
                        filters.pageRequest
                ))
                .mapTry(materials -> {
                    if (materials.getTotalElements() == 0) {
                        return Tuple.of(materials.map(FindAllPageObject::new), new HashSet<>());
                    }

                    var usersId = materials.stream().map(objects -> ((Material) objects[0]).getUserId()).collect(Collectors.toSet());
                    var users = userStore.findUsersById(usersId)
                            .map(HashSet::new)
                            .getOrElseThrow(() -> new IllegalStateException("Failed to retrieve users by ID."));

                    if (users.size() != usersId.size()) {
                        throw new IllegalStateException("Number of users does not match");
                    }

                    return Tuple.of(materials.map(FindAllPageObject::new), users);
                });
    }

    public Try<Page<FindAllPageWithUserObject>> findAllInDateRange(LocalDateTime fromDate, LocalDateTime toDate, PageRequest pageable) {
        return Try.of(() -> materialRepository.findMaterialsInDateRangeWithPage(fromDate, toDate, pageable))
                .mapTry(materials -> {
                    if (materials.getTotalElements() == 0) {
                        return materials.map(material -> new FindAllPageWithUserObject(null, null, null));
                    }

                    final var gradeMap = gradeStore
                            .findAvgGradeByMaterialsIds(materials.stream().map(Material::getId).collect(Collectors.toSet()))
                            .map(object -> object.stream().collect(Collectors.toMap(
                                    GradeStore.FindAvgGradeObject::getMaterialId, GradeStore.FindAvgGradeObject::getAvgGrade
                            )))
                            .get();
                    Set<Long> materialUserIds = materials.stream().map(Material::getUserId).collect(Collectors.toSet());
                    final var userMap = userStore.findUsersById(materialUserIds)
                            .map(users -> users.stream().collect(Collectors.toMap(
                                    UserStore.User::getId, UserStore.User::getUsername
                            )))
                            .getOrElseThrow(() -> new IllegalStateException("Failed to retrieve users by ID."));

                    if (materialUserIds.size() != userMap.keySet().size()) {
                        throw new IllegalStateException("Number of users does not match");
                    }

                    return materials.map(material -> new FindAllPageWithUserObject(
                            material,
                            gradeMap.getOrDefault(material.getId(), null),
                            userMap.getOrDefault(material.getUserId(), null)
                    ));
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

    public record FindByIdObject(Material material, String username, Double avgGrade, List<Long> gradeStats) {
    }

    @Value
    @Builder
    @RequiredArgsConstructor
    public static class FindAllFilters {
        String phrase;
        Long id;
        List<Material.MaterialStatus> statuses;
        Long sectionId;
        List<Long> categoryIds;
        List<Long> tagIds;
        Double minAvgGrade;
        Double maxAvgGrade;
        Pageable pageRequest;
    }

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor
    public static class FindAllPageObject {
        Material material;
        Double avgGrade;

        public FindAllPageObject(Object[] objects) {
            this.material = (Material) objects[0];
            this.avgGrade = (Double) objects[1];
        }
    }

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class FindAllPageWithUserObject extends FindAllPageObject {
        String username;

        public FindAllPageWithUserObject(Material material, Double avgGrade, String username) {
            super(material, avgGrade);
            this.username = username;
        }
    }
}
