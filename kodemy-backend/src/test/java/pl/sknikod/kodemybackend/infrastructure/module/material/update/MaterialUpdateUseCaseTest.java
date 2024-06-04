package pl.sknikod.kodemybackend.infrastructure.module.material.update;

import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.sknikod.kodemybackend.exception.structure.NotFoundException;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.factory.CategoryFactory;
import pl.sknikod.kodemybackend.factory.MaterialFactory;
import pl.sknikod.kodemybackend.factory.TagFactory;
import pl.sknikod.kodemybackend.factory.TypeFactory;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.database.handler.*;
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialRabbitProducer;
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialRabbitProducerTest;
import pl.sknikod.kodemybackend.util.BaseTest;
import pl.sknikod.kodemybackend.util.WithUserPrincipal;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MaterialUpdateUseCaseTest extends BaseTest {
    private final MaterialUpdateUseCase.MaterialUpdateMapper materialUpdateMapper =
            Mockito.mock(MaterialUpdateUseCase.MaterialUpdateMapper.class);
    private final MaterialRepositoryHandler materialRepositoryHandler =
            Mockito.mock(MaterialRepositoryHandler.class);
    private final TypeRepositoryHandler typeRepositoryHandler =
            Mockito.mock(TypeRepositoryHandler.class);
    private final CategoryRepositoryHandler categoryRepositoryHandler =
            Mockito.mock(CategoryRepositoryHandler.class);
    private final TagRepositoryHandler tagRepositoryHandler =
            Mockito.mock(TagRepositoryHandler.class);
    private final MaterialRabbitProducer materialProducer = new MaterialRabbitProducerTest();
    private final GradeRepositoryHandler gradeRepositoryHandler =
            Mockito.mock(GradeRepositoryHandler.class);

    private final MaterialUpdateUseCase.MaterialUpdateRequest REQUEST =
            new MaterialUpdateUseCase.MaterialUpdateRequest(
                    null, null, null, null, null, null
            );

    private final MaterialUpdateUseCase materialUseCase = new MaterialUpdateUseCase(
            materialUpdateMapper,
            categoryRepositoryHandler,
            typeRepositoryHandler,
            tagRepositoryHandler,
            materialRepositoryHandler,
            materialProducer,
            gradeRepositoryHandler
    );

    private static final Material MATERIAL = MaterialFactory.material();

    @Test
    @WithUserPrincipal
    void update_shouldSucceed() {
        // given
        var response = new MaterialUpdateUseCase.MaterialUpdateResponse(
                MATERIAL.getId(), null, null, null,null,null,null
        );
        when(materialRepositoryHandler.findById(any()))
                .thenReturn(Try.success(MATERIAL));
        when(materialRepositoryHandler.findById(REQUEST.getCategoryId()))
                .thenReturn(Try.success(MaterialFactory.material()));
        when(categoryRepositoryHandler.findById(REQUEST.getCategoryId()))
                .thenReturn(Try.success(CategoryFactory.category(1L)));
        when(typeRepositoryHandler.findById(REQUEST.getTypeId()))
                .thenReturn(Try.success(TypeFactory.type()));
        when(tagRepositoryHandler.findAllByIdIn(REQUEST.getTagsIds()))
                .thenReturn(Try.success(Set.of(TagFactory.tag())));
        when(gradeRepositoryHandler.findAvgGradeByMaterial(any()))
                .thenReturn(Try.success(1.23));
        when(materialRepositoryHandler.save(any()))
                .thenReturn(Try.success(MATERIAL));
        when(materialUpdateMapper.map(any()))
                .thenReturn(response);
        // when
        var result = materialUseCase.update(MaterialFactory.material().getId(), REQUEST);
        // then
        assertNotNull(result);
    }

    @Test
    void update_shouldThrowException_whenNotAuthorize() {
        // given
        // when & then
        assertThrows(ServerProcessingException.class, () ->
                materialUseCase.update(MaterialFactory.material().getId(), REQUEST));
    }

    @Test
    @WithUserPrincipal
    void update_shouldThrowException_whenMaterialNotFound() {
        // given
        when(materialRepositoryHandler.findById(any()))
                .thenReturn(Try.success(MATERIAL));
        when(materialRepositoryHandler.findById(any()))
                .thenReturn(Try.failure(new NotFoundException("")));
        // when & then
        assertThrows(ServerProcessingException.class, () ->
                materialUseCase.update(MaterialFactory.material().getId(), REQUEST));
    }


    @Test
    @WithUserPrincipal
    void update_shouldThrowException_whenCategoryNotFound() {
        // givenr
        when(materialRepositoryHandler.findById(any()))
                .thenReturn(Try.success(MATERIAL));
        when(categoryRepositoryHandler.findById(any()))
                .thenReturn(Try.failure(new NotFoundException("")));
        // when & then
        assertThrows(NotFoundException.class, () ->
                materialUseCase.update(MaterialFactory.material().getId(), REQUEST));
    }

    @Test
    @WithUserPrincipal
    void update_shouldThrowException_whenTypeNotFound() {
        // given
        when(materialRepositoryHandler.findById(any()))
                .thenReturn(Try.success(MATERIAL));
        when(categoryRepositoryHandler.findById(REQUEST.getCategoryId()))
                .thenReturn(Try.success(CategoryFactory.category(1L)));
        when(typeRepositoryHandler.findById(REQUEST.getTypeId()))
                .thenReturn(Try.failure(new NotFoundException("")));
        // when & then
        assertThrows(NotFoundException.class, () ->
                materialUseCase.update(MaterialFactory.material().getId(), REQUEST));
    }

    @Test
    @WithUserPrincipal
    void update_shouldThrowException_whenTagsNotFound() {
        // given
        when(materialRepositoryHandler.findById(any()))
                .thenReturn(Try.success(MATERIAL));
        when(categoryRepositoryHandler.findById(REQUEST.getCategoryId()))
                .thenReturn(Try.success(CategoryFactory.category(1L)));
        when(typeRepositoryHandler.findById(REQUEST.getTypeId()))
                .thenReturn(Try.success(TypeFactory.type()));
        when(tagRepositoryHandler.findAllByIdIn(REQUEST.getTagsIds()))
                .thenReturn(Try.failure(new NotFoundException("")));
        // when & then
        assertThrows(NotFoundException.class, () ->
                materialUseCase.update(MaterialFactory.material().getId(), REQUEST));
    }

    @Test
    @WithUserPrincipal
    void update_shouldThrowException_whenGradeAvgError() {
        // given
        when(materialRepositoryHandler.findById(any()))
                .thenReturn(Try.success(MATERIAL));
        when(categoryRepositoryHandler.findById(REQUEST.getCategoryId()))
                .thenReturn(Try.success(CategoryFactory.category(1L)));
        when(typeRepositoryHandler.findById(REQUEST.getTypeId()))
                .thenReturn(Try.success(TypeFactory.type()));
        when(tagRepositoryHandler.findAllByIdIn(REQUEST.getTagsIds()))
                .thenReturn(Try.success(Set.of(TagFactory.tag())));
        when(gradeRepositoryHandler.findAvgGradeByMaterial(any()))
                .thenReturn(Try.failure(new RuntimeException()));
        // when & then
        assertThrows(RuntimeException.class, () ->
                materialUseCase.update(MaterialFactory.material().getId(), REQUEST));
    }

    @Test
    @WithUserPrincipal
    void update_shouldThrowException_whenSaveFails() {
        // given
        when(materialRepositoryHandler.findById(any()))
                .thenReturn(Try.success(MATERIAL));
        when(categoryRepositoryHandler.findById(REQUEST.getCategoryId()))
                .thenReturn(Try.success(CategoryFactory.category(1L)));
        when(typeRepositoryHandler.findById(REQUEST.getTypeId()))
                .thenReturn(Try.success(TypeFactory.type()));
        when(tagRepositoryHandler.findAllByIdIn(REQUEST.getTagsIds()))
                .thenReturn(Try.success(Set.of(TagFactory.tag())));
        when(gradeRepositoryHandler.findAvgGradeByMaterial(any()))
                .thenReturn(Try.success(1.23));
        when(materialRepositoryHandler.save(any()))
                .thenReturn(Try.failure(new RuntimeException()));
        // when & then
        assertThrows(RuntimeException.class, () ->
                materialUseCase.update(MaterialFactory.material().getId(), REQUEST));
    }
}