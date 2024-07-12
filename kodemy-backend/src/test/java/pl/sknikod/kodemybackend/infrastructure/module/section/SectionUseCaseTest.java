package pl.sknikod.kodemybackend.infrastructure.module.section;

import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.sknikod.kodemybackend.exception.structure.NotFoundException;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.factory.SectionFactory;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.SectionMapper;
import pl.sknikod.kodemybackend.infrastructure.database.handler.SectionRepositoryHandler;
import pl.sknikod.kodemybackend.infrastructure.module.section.model.SingleSectionResponse;
import pl.sknikod.kodemybackend.BaseTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

class SectionUseCaseTest extends BaseTest {
    private final SectionRepositoryHandler sectionRepositoryHandler =
            Mockito.mock(SectionRepositoryHandler.class);
    private final SectionMapper sectionMapper =
            Mockito.mock(SectionMapper.class);

    private final SectionUseCase sectionUseCase =
            new SectionUseCase(sectionRepositoryHandler, sectionMapper);

    @Test
    void getAllSections_shouldSucceed() {
        // given
        var section = SectionFactory.section();
        var mapped = new SingleSectionResponse(section.getId(), null, null);
        when(sectionRepositoryHandler.findAll())
                .thenReturn(Try.success(List.of(section)));
        when(sectionMapper.map(anyList()))
                .thenReturn(List.of(mapped));
        // when
        var result = sectionUseCase.getAllSections();
        // then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAllSections_shouldThrowException_whenNoSectionsFound() {
        // given
        when(sectionRepositoryHandler.findAll())
                .thenReturn(Try.failure(new NotFoundException("")));
        // when & then
        assertThrows(ServerProcessingException.class, sectionUseCase::getAllSections);
    }
}