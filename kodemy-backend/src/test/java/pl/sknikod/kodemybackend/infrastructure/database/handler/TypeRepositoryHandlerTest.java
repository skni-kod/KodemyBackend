package pl.sknikod.kodemybackend.infrastructure.database.handler;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.sknikod.kodemybackend.exception.structure.NotFoundException;
import pl.sknikod.kodemybackend.factory.TypeFactory;
import pl.sknikod.kodemybackend.infrastructure.database.repository.TypeRepository;
import pl.sknikod.kodemybackend.util.BaseTest;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TypeRepositoryHandlerTest extends BaseTest {
    private final TypeRepository typeRepository =
            Mockito.mock(TypeRepository.class);

    private final TypeRepositoryHandler typeRepositoryHandler =
            new TypeRepositoryHandler(typeRepository);

    @Test
    void findById_shouldSucceed() {
        // given
        var type = TypeFactory.type();
        when(typeRepository.findById(type.getId()))
                .thenReturn(Optional.of(type));
        // when
        var result = typeRepositoryHandler.findById(type.getId());
        // then
        assertTrue(result.isSuccess());
        assertEquals(type.getId(), result.get().getId());
    }

    @Test
    void findById_shouldFailure_whenTypeNotFound() {
        // given
        when(typeRepository.findById(any()))
                .thenReturn(Optional.empty());
        // when
        var result = typeRepositoryHandler.findById(1L);
        // then
        assertTrue(result.isFailure());
        assertInstanceOf(NotFoundException.class, result.getCause());
    }

    @Test
    void findAll_shouldSucceed() {
        // given
        when(typeRepository.findAll())
                .thenReturn(Collections.emptyList());
        // when
        var result = typeRepositoryHandler.findAll();
        // then
        assertTrue(result.isSuccess());
        assertEquals(0, result.get().size());
    }

}