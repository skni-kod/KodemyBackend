package pl.sknikod.kodemybackend.infrastructure.database.handler;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.OptimisticLockingFailureException;
import pl.sknikod.kodemybackend.exception.structure.AlreadyExistsException;
import pl.sknikod.kodemybackend.exception.structure.NotFoundException;
import pl.sknikod.kodemybackend.factory.TagFactory;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Tag;
import pl.sknikod.kodemybackend.infrastructure.database.repository.TagRepository;
import pl.sknikod.kodemybackend.util.BaseTest;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TagRepositoryHandlerTest extends BaseTest {
    private final TagRepository tagRepository =
            Mockito.mock(TagRepository.class);

    private final TagRepositoryHandler tagRepositoryHandler =
            new TagRepositoryHandler(tagRepository);

    @Test
    void findAllByIdIn_shouldSucceed(){
        // given
        when(tagRepository.findTagsByIdIn(any()))
                .thenReturn(Set.of(new Tag()));
        // when
        var result = tagRepositoryHandler.findAllByIdIn(Set.of(1L));
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
        var result = tagRepositoryHandler.findAllByIdIn(Set.of(1L));
        // then
        assertTrue(result.isFailure());
        assertInstanceOf(NotFoundException.class, result.getCause());
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
        var result = tagRepositoryHandler.save(name);
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
        var result = tagRepositoryHandler.save(name);
        // then
        assertTrue(result.isFailure());
        assertInstanceOf(AlreadyExistsException.class, result.getCause());
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
        var result = tagRepositoryHandler.save(name);
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
        var result = tagRepositoryHandler.findAll();
        // then
        assertTrue(result.isSuccess());
        assertEquals(0, result.get().size());
    }
}