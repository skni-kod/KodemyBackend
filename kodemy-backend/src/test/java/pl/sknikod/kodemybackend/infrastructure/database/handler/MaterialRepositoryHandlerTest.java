package pl.sknikod.kodemybackend.infrastructure.database.handler;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.OptimisticLockingFailureException;
import pl.sknikod.kodemybackend.exception.structure.NotFoundException;
import pl.sknikod.kodemybackend.factory.MaterialFactory;
import pl.sknikod.kodemybackend.infrastructure.database.repository.MaterialRepository;
import pl.sknikod.kodemybackend.util.BaseTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MaterialRepositoryHandlerTest extends BaseTest {
    private final MaterialRepository materialRepository = Mockito.mock(MaterialRepository.class);

    private final MaterialRepositoryHandler materialRepositoryHandler =
            new MaterialRepositoryHandler(materialRepository);

    @Test
    void findById_shouldSucceed() {
        // given
        var material = MaterialFactory.material();
        when(materialRepository.findById(material.getId()))
                .thenReturn(Optional.of(material));
        // when
        var result = materialRepositoryHandler.findById(material.getId());
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
        var result = materialRepositoryHandler.findById(1L);
        // then
        assertTrue(result.isFailure());
        assertInstanceOf(NotFoundException.class, result.getCause());
    }

    @Test
    void save_shouldSuccess() {
        // given
        var material = MaterialFactory.material();
        when(materialRepository.save(any()))
                .thenReturn(material);
        // when
        var result = materialRepositoryHandler.save(material);
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
        var result = materialRepositoryHandler.save(material);
        // then
        assertTrue(result.isFailure());
        assertInstanceOf(OptimisticLockingFailureException.class, result.getCause());
    }
}