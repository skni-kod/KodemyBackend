package pl.sknikod.kodemybackend.infrastructure.database.handler;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.sknikod.kodemybackend.factory.CategoryFactory;
import pl.sknikod.kodemybackend.infrastructure.database.repository.GradeRepository;
import pl.sknikod.kodemybackend.util.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class GradeRepositoryHandlerTest extends BaseTest {
    private final GradeRepository gradeRepository =
            Mockito.mock(GradeRepository.class);

    private final GradeRepositoryHandler gradeRepositoryHandler =
            new GradeRepositoryHandler(gradeRepository);

    @Test
    void findAvgGradeByMaterial_shouldSucceed() {
        // given
        var category = CategoryFactory.category(1L);
        when(gradeRepository.findAvgGradeByMaterialId(category.getId()))
                .thenReturn(1.23);
        // when
        var result = gradeRepositoryHandler.findAvgGradeByMaterial(category.getId());
        // then
        assertTrue(result.isSuccess());
        assertEquals(1.23, result.get());
    }
}