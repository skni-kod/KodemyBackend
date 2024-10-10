package pl.sknikod.kodemybackend.infrastructure.database.handler;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.sknikod.kodemybackend.factory.TypeFactory;
import pl.sknikod.kodemybackend.infrastructure.dao.TypeDao;
import pl.sknikod.kodemybackend.infrastructure.database.TypeRepository;
import pl.sknikod.kodemybackend.BaseTest;
import pl.sknikod.kodemycommon.exception.NotFound404Exception;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TypeDaoTest extends BaseTest {
    private final TypeRepository typeRepository =
            Mockito.mock(TypeRepository.class);

    private final TypeDao typeDao =
            new TypeDao(typeRepository);

    @Test
    void findById_shouldSucceed() {
        // given
        var type = TypeFactory.type();
        when(typeRepository.findById(type.getId()))
                .thenReturn(Optional.of(type));
        // when
        var result = typeDao.findById(type.getId());
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
        var result = typeDao.findById(1L);
        // then
        assertTrue(result.isFailure());
        assertInstanceOf(NotFound404Exception.class, result.getCause());
    }

    @Test
    void findAll_shouldSucceed() {
        // given
        when(typeRepository.findAll())
                .thenReturn(Collections.emptyList());
        // when
        var result = typeDao.findAll();
        // then
        assertTrue(result.isSuccess());
        assertEquals(0, result.get().size());
    }

}