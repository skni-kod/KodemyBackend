package pl.sknikod.kodemybackend.infrastructure.module.type;

import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.sknikod.kodemybackend.factory.TypeFactory;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.TypeMapper;
import pl.sknikod.kodemybackend.infrastructure.dao.TypeDao;
import pl.sknikod.kodemybackend.infrastructure.module.type.model.SingleTypeResponse;
import pl.sknikod.kodemybackend.BaseTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

class TypeServiceTest extends BaseTest {
    private final TypeDao typeDao =
            Mockito.mock(TypeDao.class);
    private final TypeMapper typeMapper =
            Mockito.mock(TypeMapper.class);

    private final TypeService typeService =
            new TypeService(typeDao, typeMapper);

    @Test
    void getAllTypes_shouldSucceed() {
        // given
        var type = TypeFactory.type();
        var mapped = new SingleTypeResponse(type.getId(), type.getName());
        when(typeDao.findAll())
                .thenReturn(Try.success(List.of(type)));
        when(typeMapper.map(anyList()))
                .thenReturn(List.of(mapped));
        // when
        var result = typeService.getAllTypes();
        // then
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}