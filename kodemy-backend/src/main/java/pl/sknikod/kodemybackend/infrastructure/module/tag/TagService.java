package pl.sknikod.kodemybackend.infrastructure.module.tag;

import lombok.AllArgsConstructor;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.TagMapper;
import pl.sknikod.kodemybackend.infrastructure.dao.TagDao;
import pl.sknikod.kodemybackend.infrastructure.module.tag.model.TagAddRequest;
import pl.sknikod.kodemybackend.infrastructure.module.tag.model.TagAddResponse;
import pl.sknikod.kodemycommons.exception.InternalError500Exception;
import pl.sknikod.kodemycommons.exception.content.ExceptionUtil;

import java.util.List;

@AllArgsConstructor
public class TagService {
    private final TagDao tagDao;
    private final TagMapper tagMapper;

    public TagAddResponse addTag(TagAddRequest tag) {
        return tagDao.save(tag.getName())
                .map(tagMapper::map)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    public List<TagAddResponse> showTags() {
        return tagDao.findAll()
                .map(tagMapper::map)
                .getOrElseThrow(th -> new InternalError500Exception());
    }
}
