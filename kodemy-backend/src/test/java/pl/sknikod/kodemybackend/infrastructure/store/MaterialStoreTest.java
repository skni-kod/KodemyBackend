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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

class MaterialStoreTest {

    private final MaterialRepository repo = Mockito.mock(MaterialRepository.class);
    private final UserStore userStore = Mockito.mock(UserStore.class);
    private final GradeStore gradeStore = Mockito.mock(GradeStore.class);

    private final MaterialStore store = new MaterialStore(repo, userStore, gradeStore);

    private final Long ID = 1L;
    private final Material.MaterialStatus NEW_STATUS = Material.MaterialStatus.APPROVED;

    @BeforeEach
    void setUp() {
        var material = new Material();
        material.setId(ID);
        material.setUserId(ID);
        material.setStatus(Material.MaterialStatus.PENDING);

        var user = new UserStore.User();
        user.setId(ID);
        user.setUsername("username");

        Mockito.when(repo.findById(Mockito.eq(ID))).thenReturn(Optional.of(material));
        Mockito.when(userStore.findById(Mockito.eq(ID))).thenReturn(Try.success(user));
        Mockito.when(gradeStore.findAvgGradeByMaterial(Mockito.eq(ID))).thenReturn(Try.success(1.0));
        Mockito.when(gradeStore.getGradeStats(Mockito.eq(ID))).thenReturn(Try.success(Collections.emptyList()));

        Mockito.when(repo.save(Mockito.any(Material.class))).thenReturn(material);

        Mockito.when(repo.searchMaterialsWithAvgGrades(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.eq(ID),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()
        )).thenReturn(new PageImpl<>(List.<Object[]>of(new Object[]{material, 1.0}), Pageable.ofSize(1), 1));

        Mockito.doNothing().when(repo).updateStatus(Mockito.eq(ID), Mockito.eq(Material.MaterialStatus.PENDING));
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
    void save_success() {
        var result = store.save(new Material());

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertNotEquals(null, result.get().getId());
    }

    @Test
    void update_success() {
        var result = store.update(new Material());

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertNotEquals(null, result.get().getId());
    }

    @Test
    void findAll_success() {
        var result = store.findAll(new FilterSearchParams(), null, ID, null);

        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void changeStatus_success() {
        var result = store.changeStatus(ID, NEW_STATUS);

        Assertions.assertTrue(result.isSuccess());
    }
}