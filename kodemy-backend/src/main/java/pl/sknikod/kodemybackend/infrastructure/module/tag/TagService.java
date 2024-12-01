package pl.sknikod.kodemybackend.infrastructure.module.tag;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemybackend.infrastructure.mapper.TagMapper;
import pl.sknikod.kodemybackend.infrastructure.module.tag.model.TagAddRequest;
import pl.sknikod.kodemybackend.infrastructure.module.tag.model.TagAddResponse;
import pl.sknikod.kodemybackend.infrastructure.store.TagStore;
import pl.sknikod.kodemycommons.exception.content.ExceptionUtil;

import java.util.List;

@Service
@AllArgsConstructor
public class TagService {
    private final TagStore tagStore;
    private final TagMapper tagMapper;

    public TagAddResponse addTag(TagAddRequest tag) {
        return tagStore.save(tag.getName())
                .map(tagMapper::map)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    public List<TagAddResponse> showTags() {
        return tagStore.findAll()
                .map(tagMapper::map)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }
}
