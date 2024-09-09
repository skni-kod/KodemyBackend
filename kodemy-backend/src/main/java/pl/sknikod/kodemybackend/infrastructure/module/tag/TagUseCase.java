package pl.sknikod.kodemybackend.infrastructure.module.tag;

import lombok.AllArgsConstructor;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.TagMapper;
import pl.sknikod.kodemybackend.infrastructure.database.handler.TagRepositoryHandler;
import pl.sknikod.kodemybackend.infrastructure.module.tag.model.TagAddRequest;
import pl.sknikod.kodemybackend.infrastructure.module.tag.model.TagAddResponse;
import pl.sknikod.kodemycommon.exception.InternalError500Exception;
import pl.sknikod.kodemycommon.exception.content.ExceptionUtil;

import java.util.List;

@AllArgsConstructor
public class TagUseCase {
    private final TagRepositoryHandler tagRepositoryHandler;
    private final TagMapper tagMapper;

    public TagAddResponse addTag(TagAddRequest tag) {
        return tagRepositoryHandler.save(tag.getName())
                .map(tagMapper::map)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    public List<TagAddResponse> showTags() {
        return tagRepositoryHandler.findAll()
                .map(tagMapper::map)
                .getOrElseThrow(th -> new InternalError500Exception());
    }
}
