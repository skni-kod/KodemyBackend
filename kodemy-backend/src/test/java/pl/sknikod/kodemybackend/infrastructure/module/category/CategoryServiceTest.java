package pl.sknikod.kodemybackend.infrastructure.module.category;

import io.vavr.control.Try;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import pl.sknikod.kodemybackend.SuperclassTest;
import pl.sknikod.kodemybackend.infrastructure.database.Category;
import pl.sknikod.kodemybackend.infrastructure.database.Section;
import pl.sknikod.kodemybackend.infrastructure.mapper.CategoryMapper;
import pl.sknikod.kodemybackend.infrastructure.store.CategoryStore;

class CategoryServiceTest extends SuperclassTest {

    @MockBean
    private CategoryStore categoryStore;

    @Autowired
    private CategoryService categoryService;

    @Test
    void showCategoryInfo_success() {
        var category = new Category();
        category.setId(1L);
        category.setSection(new Section());
        category.setName("name");

        Mockito.when(categoryStore.findById(Mockito.eq(category.getId()))).thenReturn(Try.success(category));

        var result = categoryService.showCategoryInfo(category.getId());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(category.getId(), result.id());
        Assertions.assertEquals(category.getName(), result.name());
    }
}