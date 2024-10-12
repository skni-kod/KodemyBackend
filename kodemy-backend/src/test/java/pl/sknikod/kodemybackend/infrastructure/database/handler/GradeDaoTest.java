package pl.sknikod.kodemybackend.infrastructure.database.handler;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.sknikod.kodemybackend.factory.CategoryFactory;
import pl.sknikod.kodemybackend.infrastructure.dao.GradeDao;
import pl.sknikod.kodemybackend.infrastructure.database.GradeRepository;
import pl.sknikod.kodemybackend.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class GradeDaoTest extends BaseTest {
    private final GradeRepository gradeRepository =
            Mockito.mock(GradeRepository.class);

    private final GradeDao gradeDao =
            new GradeDao(gradeRepository);

    @Test
    void findAvgGradeByMaterial_shouldSucceed() {
        // given
        var category = CategoryFactory.category(1L);
        when(gradeRepository.findAvgGradeByMaterialId(category.getId()))
                .thenReturn(1.23);
        // when
        var result = gradeDao.findAvgGradeByMaterial(category.getId());
        // then
        assertTrue(result.isSuccess());
        assertEquals(1.23, result.get());
    }
}