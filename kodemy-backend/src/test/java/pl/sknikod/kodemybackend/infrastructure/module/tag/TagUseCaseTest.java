package pl.sknikod.kodemybackend.infrastructure.module.tag;

import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.sknikod.kodemybackend.factory.TagFactory;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.TagMapper;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Tag;
import pl.sknikod.kodemybackend.infrastructure.database.handler.TagRepositoryHandler;
import pl.sknikod.kodemybackend.infrastructure.module.tag.model.TagAddRequest;
import pl.sknikod.kodemybackend.infrastructure.module.tag.model.TagAddResponse;
import pl.sknikod.kodemybackend.util.BaseTest;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TagUseCaseTest extends BaseTest {

    final TagRepositoryHandler tagRepositoryHandler = Mockito.mock(TagRepositoryHandler.class);
    final TagUseCase tagUseCase = new TagUseCase(tagRepositoryHandler, new TagMapperImpl());

    static final TagAddRequest request = new TagAddRequest();
    static {
        request.setName("name");
    }

    @Test
    void addTags_shouldSucceed() {
        // given
        var tag = new Tag();
        tag.setName(request.getName());
        when(tagRepositoryHandler.save(any()))
                .thenReturn(Try.success(tag));
        // when
        var result = tagUseCase.addTag(request);
        // then
        assertNotNull(result);
        assertEquals(tag.getName(), result.name());
    }

    @Test
    void addTags_shouldFailure_whenSaveFails() {
        // given
        when(tagRepositoryHandler.save(any()))
                .thenReturn(Try.failure(new RuntimeException()));
        // when & then
        assertThrows(RuntimeException.class, () -> tagUseCase.addTag(request));
    }

    @Test
    void showTags_shouldSucceed() {
        // given
        var tags = List.of(TagFactory.tag());
        when(tagRepositoryHandler.findAll())
                .thenReturn(Try.success(tags));
        // when
        var result = tagUseCase.showTags();
        // then
        assertNotNull(result);
        assertEquals(tags.size(), result.size());
        assertEquals(tags.get(0).getId(), result.get(0).id());
    }

    @Test
    void showTags_shouldThrowException_whenNoTagsFound() {
        // given
        when(tagRepositoryHandler.findAll())
                .thenReturn(Try.failure(new RuntimeException()));
        // when & then
        assertThrows(RuntimeException.class, tagUseCase::showTags);
    }

    static class TagMapperImpl implements TagMapper {
        @Override
        public List<TagAddResponse> map(Collection<Tag> tag) {
            return tag.stream().map(this::map).toList();
        }

        @Override
        public TagAddResponse map(Tag tag) {
            return new TagAddResponse(tag.getId(), tag.getName());
        }
    }
}