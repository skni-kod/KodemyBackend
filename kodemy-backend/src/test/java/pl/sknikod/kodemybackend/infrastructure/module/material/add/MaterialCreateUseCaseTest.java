package pl.sknikod.kodemybackend.infrastructure.module.material.add;

import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.sknikod.kodemybackend.exception.structure.NotFoundException;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.factory.*;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.database.handler.CategoryRepositoryHandler;
import pl.sknikod.kodemybackend.infrastructure.database.handler.MaterialRepositoryHandler;
import pl.sknikod.kodemybackend.infrastructure.database.handler.TagRepositoryHandler;
import pl.sknikod.kodemybackend.infrastructure.database.handler.TypeRepositoryHandler;
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialRabbitProducer;
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialRabbitProducerTest;
import pl.sknikod.kodemybackend.BaseTest;
import pl.sknikod.kodemybackend.WithUserPrincipal;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MaterialCreateUseCaseTest extends BaseTest {
    private final MaterialRepositoryHandler materialRepositoryHandler =
            Mockito.mock(MaterialRepositoryHandler.class);
    private final TypeRepositoryHandler typeRepositoryHandler =
            Mockito.mock(TypeRepositoryHandler.class);
    private final MaterialCreateUseCase.MaterialCreateMapper createMaterialMapper =
            Mockito.mock(MaterialCreateUseCase.MaterialCreateMapper.class);
    private final CategoryRepositoryHandler categoryRepositoryHandler =
            Mockito.mock(CategoryRepositoryHandler.class);
    private final TagRepositoryHandler tagRepositoryHandler =
            Mockito.mock(TagRepositoryHandler.class);
    private final MaterialRabbitProducer materialProducer = new MaterialRabbitProducerTest();

    private final MaterialCreateUseCase.MaterialCreateRequest REQUEST =
            new MaterialCreateUseCase.MaterialCreateRequest(
                    null, null, null, null, null, null
            );

    private final MaterialCreateUseCase materialUseCase = new MaterialCreateUseCase(
            materialRepositoryHandler,
            typeRepositoryHandler,
            createMaterialMapper,
            categoryRepositoryHandler,
            tagRepositoryHandler,
            materialProducer
    );

    private static final Material MATERIAL = MaterialFactory.material();

    @Test
    @WithUserPrincipal
    void create_shouldSucceed() {
        // given
        var response = new MaterialCreateUseCase.MaterialCreateResponse(
                MATERIAL.getId(), null, null
        );
        when(categoryRepositoryHandler.findById(REQUEST.getCategoryId()))
                .thenReturn(Try.success(CategoryFactory.category(1L)));
        when(typeRepositoryHandler.findById(REQUEST.getTypeId()))
                .thenReturn(Try.success(TypeFactory.type()));
        when(tagRepositoryHandler.findAllByIdIn(REQUEST.getTagsIds()))
                .thenReturn(Try.success(Set.of(TagFactory.tag())));
        when(materialRepositoryHandler.save(any()))
                .thenReturn(Try.success(MATERIAL));
        when(createMaterialMapper.map(any()))
                .thenReturn(response);
        // when
        var result = materialUseCase.create(REQUEST);
        // then
        assertNotNull(result);
    }

    @Test
    void create_shouldThrowException_whenNotAuthorize() {
        // given
        // when & then
        assertThrows(ServerProcessingException.class, () -> materialUseCase.create(REQUEST));
    }

    @Test
    @WithUserPrincipal
    void create_shouldThrowException_whenCategoryNotFound() {
        // given
        when(categoryRepositoryHandler.findById(any()))
                .thenReturn(Try.failure(new NotFoundException("")));
        // when & then
        assertThrows(NotFoundException.class, () -> materialUseCase.create(REQUEST));
    }

    @Test
    @WithUserPrincipal
    void create_shouldThrowException_whenTypeNotFound() {
        // given
        when(categoryRepositoryHandler.findById(REQUEST.getCategoryId()))
                .thenReturn(Try.success(CategoryFactory.category(1L)));
        when(typeRepositoryHandler.findById(REQUEST.getTypeId()))
                .thenReturn(Try.failure(new NotFoundException("")));
        // when & then
        assertThrows(NotFoundException.class, () -> materialUseCase.create(REQUEST));
    }

    @Test
    @WithUserPrincipal
    void create_shouldThrowException_whenTagsNotFound() {
        // given
        var response = new MaterialCreateUseCase.MaterialCreateResponse(
                MATERIAL.getId(), null, null
        );
        when(categoryRepositoryHandler.findById(REQUEST.getCategoryId()))
                .thenReturn(Try.success(CategoryFactory.category(1L)));
        when(typeRepositoryHandler.findById(REQUEST.getTypeId()))
                .thenReturn(Try.success(TypeFactory.type()));
        when(tagRepositoryHandler.findAllByIdIn(REQUEST.getTagsIds()))
                .thenReturn(Try.failure(new NotFoundException("")));
        // when & then
        assertThrows(NotFoundException.class, () -> materialUseCase.create(REQUEST));
    }

    @Test
    @WithUserPrincipal
    void create_shouldThrowException_whenSaveFails() {
        // given
        when(categoryRepositoryHandler.findById(REQUEST.getCategoryId()))
                .thenReturn(Try.success(CategoryFactory.category(1L)));
        when(typeRepositoryHandler.findById(REQUEST.getTypeId()))
                .thenReturn(Try.success(TypeFactory.type()));
        when(tagRepositoryHandler.findAllByIdIn(REQUEST.getTagsIds()))
                .thenReturn(Try.success(Set.of(TagFactory.tag())));
        when(materialRepositoryHandler.save(any()))
                .thenReturn(Try.failure(new RuntimeException()));
        // when & then
        assertThrows(RuntimeException.class, () -> materialUseCase.create(REQUEST));
    }
}