package pl.sknikod.kodemybackend.infrastructure.module.type;

import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.sknikod.kodemybackend.factory.TypeFactory;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.TypeMapper;
import pl.sknikod.kodemybackend.infrastructure.database.handler.TypeRepositoryHandler;
import pl.sknikod.kodemybackend.infrastructure.module.type.model.SingleTypeResponse;
import pl.sknikod.kodemybackend.BaseTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

class TypeUseCaseTest extends BaseTest {
    private final TypeRepositoryHandler typeRepositoryHandler =
            Mockito.mock(TypeRepositoryHandler.class);
    private final TypeMapper typeMapper =
            Mockito.mock(TypeMapper.class);

    private final TypeUseCase typeUseCase =
            new TypeUseCase(typeRepositoryHandler, typeMapper);

    @Test
    void getAllTypes_shouldSucceed() {
        // given
        var type = TypeFactory.type();
        var mapped = new SingleTypeResponse(type.getId(), type.getName());
        when(typeRepositoryHandler.findAll())
                .thenReturn(Try.success(List.of(type)));
        when(typeMapper.map(anyList()))
                .thenReturn(List.of(mapped));
        // when
        var result = typeUseCase.getAllTypes();
        // then
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}