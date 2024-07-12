package pl.sknikod.kodemybackend.infrastructure.database.handler;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.sknikod.kodemybackend.infrastructure.database.repository.SectionRepository;
import pl.sknikod.kodemybackend.BaseTest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SectionRepositoryHandlerTest extends BaseTest {
    private final SectionRepository sectionRepository =
            Mockito.mock(SectionRepository.class);

    private final SectionRepositoryHandler sectionRepositoryHandler =
            new SectionRepositoryHandler(sectionRepository);

    @Test
    void findAll_shouldSucceed() {
        // given
        when(sectionRepository.findAll())
                .thenReturn(Collections.emptyList());
        // when
        var result = sectionRepositoryHandler.findAll();
        // then
        assertTrue(result.isSuccess());
        assertEquals(0, result.get().size());
    }
}