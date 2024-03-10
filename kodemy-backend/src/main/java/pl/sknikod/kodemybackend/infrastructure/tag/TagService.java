package pl.sknikod.kodemybackend.infrastructure.tag;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemybackend.exception.structure.AlreadyExistsException;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Tag;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.TagMapper;
import pl.sknikod.kodemybackend.infrastructure.common.repository.TagRepository;
import pl.sknikod.kodemybackend.infrastructure.tag.rest.TagAddRequest;
import pl.sknikod.kodemybackend.infrastructure.tag.rest.TagAddResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public TagAddResponse addTag(TagAddRequest tag) {
        tagRepository
                .findByName(tag.getName())
                .ifPresent(found -> {
                    throw new AlreadyExistsException(AlreadyExistsException.Format.FIELD, Tag.class, tag.getName());
                });
        return Option.of(tag)
                .map(tagMapper::map)
                .peek(tagRepository::save)
                .map(tagMapper::map)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Tag.class));
    }

    public List<TagAddResponse> showTags() {
        return tagRepository
                .findAll()
                .parallelStream()
                .map(tagMapper::map)
                .collect(Collectors.toList());
    }
}
