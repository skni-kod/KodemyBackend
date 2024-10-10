package pl.sknikod.kodemybackend.infrastructure.module.material.update;

import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.sknikod.kodemybackend.factory.CategoryFactory;
import pl.sknikod.kodemybackend.factory.MaterialFactory;
import pl.sknikod.kodemybackend.factory.TagFactory;
import pl.sknikod.kodemybackend.factory.TypeFactory;
import pl.sknikod.kodemybackend.infrastructure.dao.*;
import pl.sknikod.kodemybackend.infrastructure.database.dao.*;
import pl.sknikod.kodemybackend.infrastructure.database.Material;
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialRabbitProducer;
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialRabbitProducerTest;
import pl.sknikod.kodemybackend.BaseTest;
import pl.sknikod.kodemybackend.WithUserPrincipal;
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialUpdateService;
import pl.sknikod.kodemycommon.exception.InternalError500Exception;
import pl.sknikod.kodemycommon.exception.NotFound404Exception;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MaterialUpdateServiceTest extends BaseTest {
    private final MaterialUpdateService.MaterialUpdateMapper materialUpdateMapper =
            Mockito.mock(MaterialUpdateService.MaterialUpdateMapper.class);
    private final MaterialDao materialDao =
            Mockito.mock(MaterialDao.class);
    private final TypeDao typeDao =
            Mockito.mock(TypeDao.class);
    private final CategoryDao categoryDao =
            Mockito.mock(CategoryDao.class);
    private final TagDao tagDao =
            Mockito.mock(TagDao.class);
    private final MaterialRabbitProducer materialProducer = new MaterialRabbitProducerTest();
    private final GradeDao gradeDao =
            Mockito.mock(GradeDao.class);

    private final MaterialUpdateService.MaterialUpdateRequest REQUEST =
            new MaterialUpdateService.MaterialUpdateRequest(
                    null, null, null, null, null, null
            );

    private final MaterialUpdateService materialService = new MaterialUpdateService(
            materialUpdateMapper,
            categoryDao,
            typeDao,
            tagDao,
            materialDao,
            materialProducer,
            gradeDao
    );

    private static final Material MATERIAL = MaterialFactory.material();

    @Test
    @WithUserPrincipal
    void update_shouldSucceed() {
        // given
        var response = new MaterialUpdateService.MaterialUpdateResponse(
                MATERIAL.getId(), null, null, null,null,null,null
        );
        when(materialDao.findById(any()))
                .thenReturn(Try.success(MATERIAL));
        when(materialDao.findById(REQUEST.getCategoryId()))
                .thenReturn(Try.success(MaterialFactory.material()));
        when(categoryDao.findById(REQUEST.getCategoryId()))
                .thenReturn(Try.success(CategoryFactory.category(1L)));
        when(typeDao.findById(REQUEST.getTypeId()))
                .thenReturn(Try.success(TypeFactory.type()));
        when(tagDao.findAllByIdIn(REQUEST.getTagsIds()))
                .thenReturn(Try.success(Set.of(TagFactory.tag())));
        when(gradeDao.findAvgGradeByMaterial(any()))
                .thenReturn(Try.success(1.23));
        when(materialDao.save(any()))
                .thenReturn(Try.success(MATERIAL));
        when(materialUpdateMapper.map(any()))
                .thenReturn(response);
        // when
        var result = materialService.update(MaterialFactory.material().getId(), REQUEST);
        // then
        assertNotNull(result);
    }

    @Test
    void update_shouldThrowException_whenNotAuthorize() {
        // given
        // when & then
        assertThrows(InternalError500Exception.class, () ->
                materialService.update(MaterialFactory.material().getId(), REQUEST));
    }

    @Test
    @WithUserPrincipal
    void update_shouldThrowException_whenMaterialNotFound() {
        // given
        when(materialDao.findById(any()))
                .thenReturn(Try.success(MATERIAL));
        when(materialDao.findById(any()))
                .thenReturn(Try.failure(new NotFound404Exception("")));
        // when & then
        assertThrows(InternalError500Exception.class, () ->
                materialService.update(MaterialFactory.material().getId(), REQUEST));
    }


    @Test
    @WithUserPrincipal
    void update_shouldThrowException_whenCategoryNotFound() {
        // givenr
        when(materialDao.findById(any()))
                .thenReturn(Try.success(MATERIAL));
        when(categoryDao.findById(any()))
                .thenReturn(Try.failure(new NotFound404Exception("")));
        // when & then
        assertThrows(NotFound404Exception.class, () ->
                materialService.update(MaterialFactory.material().getId(), REQUEST));
    }

    @Test
    @WithUserPrincipal
    void update_shouldThrowException_whenTypeNotFound() {
        // given
        when(materialDao.findById(any()))
                .thenReturn(Try.success(MATERIAL));
        when(categoryDao.findById(REQUEST.getCategoryId()))
                .thenReturn(Try.success(CategoryFactory.category(1L)));
        when(typeDao.findById(REQUEST.getTypeId()))
                .thenReturn(Try.failure(new NotFound404Exception("")));
        // when & then
        assertThrows(NotFound404Exception.class, () ->
                materialService.update(MaterialFactory.material().getId(), REQUEST));
    }

    @Test
    @WithUserPrincipal
    void update_shouldThrowException_whenTagsNotFound() {
        // given
        when(materialDao.findById(any()))
                .thenReturn(Try.success(MATERIAL));
        when(categoryDao.findById(REQUEST.getCategoryId()))
                .thenReturn(Try.success(CategoryFactory.category(1L)));
        when(typeDao.findById(REQUEST.getTypeId()))
                .thenReturn(Try.success(TypeFactory.type()));
        when(tagDao.findAllByIdIn(REQUEST.getTagsIds()))
                .thenReturn(Try.failure(new NotFound404Exception("")));
        // when & then
        assertThrows(NotFound404Exception.class, () ->
                materialService.update(MaterialFactory.material().getId(), REQUEST));
    }

    @Test
    @WithUserPrincipal
    void update_shouldThrowException_whenGradeAvgError() {
        // given
        when(materialDao.findById(any()))
                .thenReturn(Try.success(MATERIAL));
        when(categoryDao.findById(REQUEST.getCategoryId()))
                .thenReturn(Try.success(CategoryFactory.category(1L)));
        when(typeDao.findById(REQUEST.getTypeId()))
                .thenReturn(Try.success(TypeFactory.type()));
        when(tagDao.findAllByIdIn(REQUEST.getTagsIds()))
                .thenReturn(Try.success(Set.of(TagFactory.tag())));
        when(gradeDao.findAvgGradeByMaterial(any()))
                .thenReturn(Try.failure(new RuntimeException()));
        // when & then
        assertThrows(RuntimeException.class, () ->
                materialService.update(MaterialFactory.material().getId(), REQUEST));
    }

    @Test
    @WithUserPrincipal
    void update_shouldThrowException_whenSaveFails() {
        // given
        when(materialDao.findById(any()))
                .thenReturn(Try.success(MATERIAL));
        when(categoryDao.findById(REQUEST.getCategoryId()))
                .thenReturn(Try.success(CategoryFactory.category(1L)));
        when(typeDao.findById(REQUEST.getTypeId()))
                .thenReturn(Try.success(TypeFactory.type()));
        when(tagDao.findAllByIdIn(REQUEST.getTagsIds()))
                .thenReturn(Try.success(Set.of(TagFactory.tag())));
        when(gradeDao.findAvgGradeByMaterial(any()))
                .thenReturn(Try.success(1.23));
        when(materialDao.save(any()))
                .thenReturn(Try.failure(new RuntimeException()));
        // when & then
        assertThrows(RuntimeException.class, () ->
                materialService.update(MaterialFactory.material().getId(), REQUEST));
    }
}