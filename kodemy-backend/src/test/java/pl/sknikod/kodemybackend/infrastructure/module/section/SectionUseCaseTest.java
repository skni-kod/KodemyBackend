package pl.sknikod.kodemybackend.infrastructure.module.section;

import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.sknikod.kodemybackend.factory.SectionFactory;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.SectionMapper;
import pl.sknikod.kodemybackend.infrastructure.dao.SectionDao;
import pl.sknikod.kodemybackend.infrastructure.module.section.model.SingleSectionResponse;
import pl.sknikod.kodemybackend.BaseTest;
import pl.sknikod.kodemycommons.exception.InternalError500Exception;
import pl.sknikod.kodemycommons.exception.NotFound404Exception;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

class SectionServiceTest extends BaseTest {
    private final SectionDao sectionDao =
            Mockito.mock(SectionDao.class);
    private final SectionMapper sectionMapper =
            Mockito.mock(SectionMapper.class);

    private final SectionService sectionService =
            new SectionService(sectionDao, sectionMapper);

    @Test
    void getAllSections_shouldSucceed() {
        // given
        var section = SectionFactory.section();
        var mapped = new SingleSectionResponse(section.getId(), null, null);
        when(sectionDao.findAll())
                .thenReturn(Try.success(List.of(section)));
        when(sectionMapper.map(anyList()))
                .thenReturn(List.of(mapped));
        // when
        var result = sectionService.getAllSections();
        // then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAllSections_shouldThrowException_whenNoSectionsFound() {
        // given
        when(sectionDao.findAll())
                .thenReturn(Try.failure(new NotFound404Exception("")));
        // when & then
        assertThrows(InternalError500Exception.class, sectionService::getAllSections);
    }
}