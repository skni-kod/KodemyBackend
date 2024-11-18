package pl.sknikod.kodemybackend.infrastructure.store;

import io.vavr.control.Try;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pl.sknikod.kodemybackend.infrastructure.database.Material;
import pl.sknikod.kodemybackend.infrastructure.database.MaterialRepository;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.FilterSearchParams;
import pl.sknikod.kodemycommons.exception.InternalError500Exception;
import pl.sknikod.kodemycommons.exception.NotFound404Exception;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static pl.sknikod.kodemybackend.infrastructure.database.Material.MaterialStatus.*;

class MaterialStoreTest {

    private final MaterialRepository repo = Mockito.mock(MaterialRepository.class);
    private final UserStore userStore = Mockito.mock(UserStore.class);
    private final GradeStore gradeStore = Mockito.mock(GradeStore.class);

    private final MaterialStore store = new MaterialStore(repo, userStore, gradeStore);

    private final Long ID = 1L;
    private final Material.MaterialStatus NEW_STATUS = APPROVED;

    @BeforeEach
    void setUp() {
        var material = new Material();
        material.setId(ID);
        material.setUserId(ID);
        material.setStatus(PENDING);

        Mockito.when(repo.findById(Mockito.eq(ID))).thenReturn(Optional.of(material));

        var user = new UserStore.User();
        user.setId(ID);
        user.setUsername("username");
        Mockito.when(userStore.findById(Mockito.eq(ID))).thenReturn(Try.success(user));

        Mockito.when(gradeStore.findAvgGradeByMaterial(Mockito.eq(ID))).thenReturn(Try.success(1.0));
        Mockito.when(gradeStore.getGradeStats(Mockito.eq(ID))).thenReturn(Try.success(Collections.emptyList()));

        Mockito.when(repo.save(Mockito.any(Material.class))).thenReturn(material);

        Mockito.when(repo.searchMaterialsWithAvgGrades(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.eq(ID),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()
        )).thenReturn(new PageImpl<>(List.<Object[]>of(new Object[]{material, 1.0}), Pageable.ofSize(1), 1));

        Mockito.doNothing().when(repo).updateStatus(Mockito.eq(ID), Mockito.eq(NEW_STATUS));
    }


    @Test
    void findById_success() {
        var result1 = store.findById(ID, true);
        var result2 = store.findById(ID);

        Assertions.assertTrue(result1.isSuccess());
        Assertions.assertEquals(ID, result1.get().getMaterial().getId());
        Assertions.assertTrue(result2.isSuccess());
        Assertions.assertEquals(ID, result2.get().getMaterial().getId());
    }

    @Test
    void findById_notFound() {
        var id = 2L;

        Mockito.when(repo.findById(Mockito.eq(id))).thenReturn(Optional.empty());

        var result1 = store.findById(id, true);
        var result2 = store.findById(id);

        Assertions.assertTrue(result1.isFailure());
        Assertions.assertInstanceOf(NotFound404Exception.class, result2.getCause());
        Assertions.assertTrue(result2.isFailure());
        Assertions.assertInstanceOf(NotFound404Exception.class, result2.getCause());
    }

    @Test
    void findById_userStoreError() {
        Mockito.when(userStore.findById(Mockito.eq(ID))).thenReturn(Try.failure(new InternalError500Exception()));

        var result = store.findById(ID);

        Assertions.assertTrue(result.isFailure());
        Assertions.assertInstanceOf(InternalError500Exception.class, result.getCause());
    }

    @Test
    void save_success() {
        var result = store.save(new Material());

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertNotEquals(null, result.get().getId());
    }

    @Test
    void save_error() {
        Mockito.when(repo.save(Mockito.any(Material.class))).thenThrow(new RuntimeException());

        var result = store.save(new Material());

        Assertions.assertTrue(result.isFailure());
        Assertions.assertInstanceOf(RuntimeException.class, result.getCause());
    }

    @Test
    void update_success() {
        var result = store.update(new Material());

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertNotEquals(null, result.get().getId());
    }

    @Test
    void update_error() {
        Mockito.when(repo.save(Mockito.any(Material.class))).thenThrow(new RuntimeException());

        var result = store.update(new Material());

        Assertions.assertTrue(result.isFailure());
        Assertions.assertInstanceOf(RuntimeException.class, result.getCause());
    }

    @Test
    void findAll_success() {
        var result = store.findAll(new FilterSearchParams(), null, ID, null);

        Assertions.assertFalse(result.isEmpty());

    }

    @Test
    void findAll_userStoreError() {
        Mockito.when(userStore.findById(Mockito.eq(ID))).thenReturn(Try.failure(new InternalError500Exception()));

        var result = store.findAll(new FilterSearchParams(), null, ID, null);

        Assertions.assertTrue(result.isFailure());
        Assertions.assertInstanceOf(InternalError500Exception.class, result.getCause());
    }

    @Test
    void findAll_error() {
        Mockito.when(repo.searchMaterialsWithAvgGrades(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.eq(ID),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()
        )).thenThrow(new RuntimeException());

        var result = store.findAll(new FilterSearchParams(), null, ID, null);

        Assertions.assertTrue(result.isFailure());
    }

    @Test
    void changeStatus_success() {
        var result = store.changeStatus(ID, NEW_STATUS);

        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void changeStatus_updateError() {
        var id = 2L;
        Mockito.doThrow(new RuntimeException()).when(repo).updateStatus(Mockito.eq(id), Mockito.eq(NEW_STATUS));

        var result = store.changeStatus(id, NEW_STATUS);

        Assertions.assertTrue(result.isFailure());
    }
}