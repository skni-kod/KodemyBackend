package pl.sknikod.kodemybackend.infrastructure.database.handler;

import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.sknikod.kodemybackend.factory.CategoryFactory;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Category;
import pl.sknikod.kodemybackend.infrastructure.database.repository.CategoryRepository;
import pl.sknikod.kodemybackend.BaseTest;
import pl.sknikod.kodemycommon.exception.NotFound404Exception;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CategoryRepositoryHandlerTest extends BaseTest {
    private final CategoryRepository categoryRepository =
            Mockito.mock(CategoryRepository.class);

    private final CategoryRepositoryHandler categoryRepositoryHandler =
            new CategoryRepositoryHandler(categoryRepository);

    @Test
    void findById_shouldSucceed() {
        // given
        var category = CategoryFactory.category(1L);
        when(categoryRepository.findById(category.getId()))
                .thenReturn(Optional.of(category));
        // when
        Try<Category> result = categoryRepositoryHandler.findById(category.getId());
        // then
        assertTrue(result.isSuccess());
        assertEquals(category.getId(), result.get().getId());
    }

    @Test
    void findById_shouldFailure_whenRoleNotFound() {
        // given
        when(categoryRepository.findById(any()))
                .thenReturn(Optional.empty());
        // when
        Try<Category> result = categoryRepositoryHandler.findById(1L);
        // then
        assertTrue(result.isFailure());
        assertInstanceOf(NotFound404Exception.class, result.getCause());
    }
}