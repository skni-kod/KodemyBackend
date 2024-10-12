package pl.sknikod.kodemybackend.infrastructure.database.handler;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.sknikod.kodemybackend.infrastructure.dao.SectionDao;
import pl.sknikod.kodemybackend.infrastructure.database.SectionRepository;
import pl.sknikod.kodemybackend.BaseTest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SectionDaoTest extends BaseTest {
    private final SectionRepository sectionRepository =
            Mockito.mock(SectionRepository.class);

    private final SectionDao sectionDao =
            new SectionDao(sectionRepository);

    @Test
    void findAll_shouldSucceed() {
        // given
        when(sectionRepository.findAll())
                .thenReturn(Collections.emptyList());
        // when
        var result = sectionDao.findAll();
        // then
        assertTrue(result.isSuccess());
        assertEquals(0, result.get().size());
    }
}