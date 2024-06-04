package pl.sknikod.kodemybackend.infrastructure.module.tag;

import lombok.AllArgsConstructor;
import pl.sknikod.kodemybackend.exception.ExceptionUtil;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.TagMapper;
import pl.sknikod.kodemybackend.infrastructure.database.handler.TagRepositoryHandler;
import pl.sknikod.kodemybackend.infrastructure.module.tag.model.TagAddRequest;
import pl.sknikod.kodemybackend.infrastructure.module.tag.model.TagAddResponse;

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
                .getOrElseThrow(th -> new ServerProcessingException());
    }
}
