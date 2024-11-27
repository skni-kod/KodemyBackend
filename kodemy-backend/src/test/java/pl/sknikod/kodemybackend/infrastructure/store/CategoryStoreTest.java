package pl.sknikod.kodemybackend.infrastructure.store;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.sknikod.kodemybackend.infrastructure.database.Category;
import pl.sknikod.kodemybackend.infrastructure.database.CategoryRepository;
import pl.sknikod.kodemycommons.exception.NotFound404Exception;

import java.util.Optional;

class CategoryStoreTest {

    private final CategoryRepository repo = Mockito.mock(CategoryRepository.class);
    private final CategoryStore store = new CategoryStore(repo);

    @Test
    void findById_success() {
        Mockito.when(repo.findById(Mockito.any(Long.class))).thenReturn(Optional.of(new Category()));

        var result = store.findById(1L);

        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void findById_notFound() {
        Mockito.when(repo.findById(Mockito.any(Long.class))).thenReturn(Optional.empty());

        var result = store.findById(1L);

        Assertions.assertTrue(result.isEmpty());
        Assertions.assertInstanceOf(NotFound404Exception.class, result.getCause());
    }
}