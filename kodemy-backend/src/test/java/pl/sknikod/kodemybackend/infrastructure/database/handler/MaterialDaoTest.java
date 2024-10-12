package pl.sknikod.kodemybackend.infrastructure.database.handler;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.OptimisticLockingFailureException;
import pl.sknikod.kodemybackend.factory.MaterialFactory;
import pl.sknikod.kodemybackend.infrastructure.dao.MaterialDao;
import pl.sknikod.kodemybackend.infrastructure.database.MaterialRepository;
import pl.sknikod.kodemybackend.BaseTest;
import pl.sknikod.kodemycommons.exception.NotFound404Exception;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MaterialDaoTest extends BaseTest {
    private final MaterialRepository materialRepository = Mockito.mock(MaterialRepository.class);

    private final MaterialDao materialDao =
            new MaterialDao(materialRepository);

    @Test
    void findById_shouldSucceed() {
        // given
        var material = MaterialFactory.material();
        when(materialRepository.findById(material.getId()))
                .thenReturn(Optional.of(material));
        // when
        var result = materialDao.findById(material.getId());
        // then
        assertTrue(result.isSuccess());
        assertEquals(material.getId(), result.get().getId());
    }

    @Test
    void findById_shouldFailure_whenMaterialNotFound() {
        // given
        when(materialRepository.findById(any()))
                .thenReturn(Optional.empty());
        // when
        var result = materialDao.findById(1L);
        // then
        assertTrue(result.isFailure());
        assertInstanceOf(NotFound404Exception.class, result.getCause());
    }

    @Test
    void save_shouldSuccess() {
        // given
        var material = MaterialFactory.material();
        when(materialRepository.save(any()))
                .thenReturn(material);
        // when
        var result = materialDao.save(material);
        // then
        assertTrue(result.isSuccess());
        assertEquals(material.getId(), result.get().getId());
    }

    @Test
    void save_shouldFailure_whenSaveFails() {
        // given
        var material = MaterialFactory.material();
        when(materialRepository.save(any()))
                .thenThrow(new OptimisticLockingFailureException("error"));
        // when
        var result = materialDao.save(material);
        // then
        assertTrue(result.isFailure());
        assertInstanceOf(OptimisticLockingFailureException.class, result.getCause());
    }
}