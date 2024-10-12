package pl.sknikod.kodemybackend.infrastructure.module.tag;

import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.sknikod.kodemybackend.factory.TagFactory;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.TagMapper;
import pl.sknikod.kodemybackend.infrastructure.database.Tag;
import pl.sknikod.kodemybackend.infrastructure.dao.TagDao;
import pl.sknikod.kodemybackend.infrastructure.module.tag.model.TagAddRequest;
import pl.sknikod.kodemybackend.infrastructure.module.tag.model.TagAddResponse;
import pl.sknikod.kodemybackend.BaseTest;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TagServiceTest extends BaseTest {

    final TagDao tagDao = Mockito.mock(TagDao.class);
    final TagService tagService = new TagService(tagDao, new TagMapperImpl());

    static final TagAddRequest request = new TagAddRequest();
    static {
        request.setName("name");
    }

    @Test
    void addTags_shouldSucceed() {
        // given
        var tag = new Tag();
        tag.setName(request.getName());
        when(tagDao.save(any()))
                .thenReturn(Try.success(tag));
        // when
        var result = tagService.addTag(request);
        // then
        assertNotNull(result);
        assertEquals(tag.getName(), result.name());
    }

    @Test
    void addTags_shouldFailure_whenSaveFails() {
        // given
        when(tagDao.save(any()))
                .thenReturn(Try.failure(new RuntimeException()));
        // when & then
        assertThrows(RuntimeException.class, () -> tagService.addTag(request));
    }

    @Test
    void showTags_shouldSucceed() {
        // given
        var tags = List.of(TagFactory.tag());
        when(tagDao.findAll())
                .thenReturn(Try.success(tags));
        // when
        var result = tagService.showTags();
        // then
        assertNotNull(result);
        assertEquals(tags.size(), result.size());
        assertEquals(tags.get(0).getId(), result.get(0).id());
    }

    @Test
    void showTags_shouldThrowException_whenNoTagsFound() {
        // given
        when(tagDao.findAll())
                .thenReturn(Try.failure(new RuntimeException()));
        // when & then
        assertThrows(RuntimeException.class, tagService::showTags);
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