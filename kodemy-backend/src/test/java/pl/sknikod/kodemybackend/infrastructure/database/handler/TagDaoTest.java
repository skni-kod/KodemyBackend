package pl.sknikod.kodemybackend.infrastructure.database.handler;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.OptimisticLockingFailureException;
import pl.sknikod.kodemybackend.factory.TagFactory;
import pl.sknikod.kodemybackend.infrastructure.dao.TagDao;
import pl.sknikod.kodemybackend.infrastructure.database.Tag;
import pl.sknikod.kodemybackend.infrastructure.database.TagRepository;
import pl.sknikod.kodemybackend.BaseTest;
import pl.sknikod.kodemycommons.exception.AlreadyExists409Exception;
import pl.sknikod.kodemycommons.exception.NotFound404Exception;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TagDaoTest extends BaseTest {
    private final TagRepository tagRepository =
            Mockito.mock(TagRepository.class);

    private final TagDao tagDao =
            new TagDao(tagRepository);

    @Test
    void findAllByIdIn_shouldSucceed(){
        // given
        when(tagRepository.findTagsByIdIn(any()))
                .thenReturn(Set.of(new Tag()));
        // when
        var result = tagDao.findAllByIdIn(Set.of(1L));
        // then
        assertTrue(result.isSuccess());
        assertEquals(1, result.get().size());
    }

    @Test
    void findAllByIdIn_shouldFailure_whenAnyTagNotFound(){
        // given
        when(tagRepository.findTagsByIdIn(any()))
                .thenReturn(Collections.emptySet());
        // when
        var result = tagDao.findAllByIdIn(Set.of(1L));
        // then
        assertTrue(result.isFailure());
        assertInstanceOf(NotFound404Exception.class, result.getCause());
    }

    @Test
    void save_shouldSucceed() {
        // given
        var name = "newTag";
        var tag = TagFactory.tag(name);
        when(tagRepository.existsByName(any()))
                .thenReturn(false);
        when(tagRepository.save(any()))
                .thenReturn(tag);
        // when
        var result = tagDao.save(name);
        // then
        assertTrue(result.isSuccess());
        assertEquals(name, result.get().getName());
    }

    @Test
    void save_shouldFailure_whenTagExists() {
        // given
        var name = "newTag";
        when(tagRepository.existsByName(any()))
                .thenReturn(true);
        // when
        var result = tagDao.save(name);
        // then
        assertTrue(result.isFailure());
        assertInstanceOf(AlreadyExists409Exception.class, result.getCause());
    }

    @Test
    void save_shouldFailure_whenSaveFails() {
        // given
        var name = "newTag";
        when(tagRepository.existsByName(any()))
                .thenReturn(false);
        when(tagRepository.save(any()))
                .thenThrow(new OptimisticLockingFailureException("error"));
        // when
        var result = tagDao.save(name);
        // then
        assertTrue(result.isFailure());
        assertInstanceOf(OptimisticLockingFailureException.class, result.getCause());
        assertEquals("error", result.getCause().getMessage());
    }

    @Test
    void findAll_shouldSucceed() {
        // given
        when(tagRepository.findAll())
                .thenReturn(Collections.emptyList());
        // when
        var result = tagDao.findAll();
        // then
        assertTrue(result.isSuccess());
        assertEquals(0, result.get().size());
    }
}