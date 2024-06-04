package pl.sknikod.kodemybackend.infrastructure.module.category;

import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.sknikod.kodemybackend.exception.structure.NotFoundException;
import pl.sknikod.kodemybackend.factory.CategoryFactory;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.CategoryMapper;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Category;
import pl.sknikod.kodemybackend.infrastructure.database.handler.CategoryRepositoryHandler;
import pl.sknikod.kodemybackend.infrastructure.database.repository.CategoryRepository;
import pl.sknikod.kodemybackend.infrastructure.module.category.model.SingleCategoryResponse;
import pl.sknikod.kodemybackend.util.BaseTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CategoryUseCaseTest extends BaseTest {
    final CategoryUseCase categoryUseCase = new CategoryUseCase(new TestCategoryRepositoryHandler(), new CategoryMapperImpl());

    @Test
    void showCategoryInfo_shouldSucceed() {
        // given
        var id = 1L;
        // when
        var result = categoryUseCase.showCategoryInfo(id);
        // then
        assertNotNull(result);
        assertEquals(id, result.id());
    }

    @Test
    void showCategoryInfo_shouldThrowException_whenCategoryNotFound() {
        // given
        // when & then
        assertThrows(NotFoundException.class, () -> categoryUseCase.showCategoryInfo(2L));
    }

    static class CategoryMapperImpl implements CategoryMapper {
        @Override
        public SingleCategoryResponse map(Category category) {
            return new SingleCategoryResponse(category.getId(), null, null);
        }
    }

    static class TestCategoryRepositoryHandler extends CategoryRepositoryHandler {
        public TestCategoryRepositoryHandler() {
            super(null);
        }

        @Override
        public Try<Category> findById(Long id) {
            if (id == 1) return Try.success(CategoryFactory.category(id));
            throw new NotFoundException("");
        }
    }
}