package pl.sknikod.kodemybackend.infrastructure.module.material.add;

import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.sknikod.kodemybackend.factory.*;
import pl.sknikod.kodemybackend.infrastructure.database.Material;
import pl.sknikod.kodemybackend.infrastructure.dao.CategoryDao;
import pl.sknikod.kodemybackend.infrastructure.dao.MaterialDao;
import pl.sknikod.kodemybackend.infrastructure.dao.TagDao;
import pl.sknikod.kodemybackend.infrastructure.dao.TypeDao;
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialCreateService;
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialRabbitProducer;
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialRabbitProducerTest;
import pl.sknikod.kodemybackend.BaseTest;
import pl.sknikod.kodemybackend.WithUserPrincipal;
import pl.sknikod.kodemycommon.exception.InternalError500Exception;
import pl.sknikod.kodemycommon.exception.NotFound404Exception;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MaterialCreateServiceTest extends BaseTest {
    private final MaterialDao materialDao =
            Mockito.mock(MaterialDao.class);
    private final TypeDao typeDao =
            Mockito.mock(TypeDao.class);
    private final MaterialCreateService.MaterialCreateMapper createMaterialMapper =
            Mockito.mock(MaterialCreateService.MaterialCreateMapper.class);
    private final CategoryDao categoryDao =
            Mockito.mock(CategoryDao.class);
    private final TagDao tagDao =
            Mockito.mock(TagDao.class);
    private final MaterialRabbitProducer materialProducer = new MaterialRabbitProducerTest();

    private final MaterialCreateService.MaterialCreateRequest REQUEST =
            new MaterialCreateService.MaterialCreateRequest(
                    null, null, null, null, null, null
            );

    private final MaterialCreateService materialService = new MaterialCreateService(
            materialDao,
            typeDao,
            createMaterialMapper,
            categoryDao,
            tagDao,
            materialProducer
    );

    private static final Material MATERIAL = MaterialFactory.material();

    @Test
    @WithUserPrincipal
    void create_shouldSucceed() {
        // given
        var response = new MaterialCreateService.MaterialCreateResponse(
                MATERIAL.getId(), null, null
        );
        when(categoryDao.findById(REQUEST.getCategoryId()))
                .thenReturn(Try.success(CategoryFactory.category(1L)));
        when(typeDao.findById(REQUEST.getTypeId()))
                .thenReturn(Try.success(TypeFactory.type()));
        when(tagDao.findAllByIdIn(REQUEST.getTagsIds()))
                .thenReturn(Try.success(Set.of(TagFactory.tag())));
        when(materialDao.save(any()))
                .thenReturn(Try.success(MATERIAL));
        when(createMaterialMapper.map(any()))
                .thenReturn(response);
        // when
        var result = materialService.create(REQUEST);
        // then
        assertNotNull(result);
    }

    @Test
    void create_shouldThrowException_whenNotAuthorize() {
        // given
        // when & then
        assertThrows(InternalError500Exception.class, () -> materialService.create(REQUEST));
    }

    @Test
    @WithUserPrincipal
    void create_shouldThrowException_whenCategoryNotFound() {
        // given
        when(categoryDao.findById(any()))
                .thenReturn(Try.failure(new NotFound404Exception("")));
        // when & then
        assertThrows(NotFound404Exception.class, () -> materialService.create(REQUEST));
    }

    @Test
    @WithUserPrincipal
    void create_shouldThrowException_whenTypeNotFound() {
        // given
        when(categoryDao.findById(REQUEST.getCategoryId()))
                .thenReturn(Try.success(CategoryFactory.category(1L)));
        when(typeDao.findById(REQUEST.getTypeId()))
                .thenReturn(Try.failure(new NotFound404Exception("")));
        // when & then
        assertThrows(NotFound404Exception.class, () -> materialService.create(REQUEST));
    }

    @Test
    @WithUserPrincipal
    void create_shouldThrowException_whenTagsNotFound() {
        // given
        var response = new MaterialCreateService.MaterialCreateResponse(
                MATERIAL.getId(), null, null
        );
        when(categoryDao.findById(REQUEST.getCategoryId()))
                .thenReturn(Try.success(CategoryFactory.category(1L)));
        when(typeDao.findById(REQUEST.getTypeId()))
                .thenReturn(Try.success(TypeFactory.type()));
        when(tagDao.findAllByIdIn(REQUEST.getTagsIds()))
                .thenReturn(Try.failure(new NotFound404Exception("")));
        // when & then
        assertThrows(NotFound404Exception.class, () -> materialService.create(REQUEST));
    }

    @Test
    @WithUserPrincipal
    void create_shouldThrowException_whenSaveFails() {
        // given
        when(categoryDao.findById(REQUEST.getCategoryId()))
                .thenReturn(Try.success(CategoryFactory.category(1L)));
        when(typeDao.findById(REQUEST.getTypeId()))
                .thenReturn(Try.success(TypeFactory.type()));
        when(tagDao.findAllByIdIn(REQUEST.getTagsIds()))
                .thenReturn(Try.success(Set.of(TagFactory.tag())));
        when(materialDao.save(any()))
                .thenReturn(Try.failure(new RuntimeException()));
        // when & then
        assertThrows(RuntimeException.class, () -> materialService.create(REQUEST));
    }
}