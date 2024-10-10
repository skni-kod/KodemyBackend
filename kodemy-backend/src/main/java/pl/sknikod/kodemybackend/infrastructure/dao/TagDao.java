package pl.sknikod.kodemybackend.infrastructure.dao;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.infrastructure.database.Tag;
import pl.sknikod.kodemybackend.infrastructure.database.TagRepository;
import pl.sknikod.kodemycommon.exception.AlreadyExists409Exception;
import pl.sknikod.kodemycommon.exception.NotFound404Exception;
import pl.sknikod.kodemycommon.exception.content.ExceptionMsgPattern;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TagDao {
    private final TagRepository tagRepository;

    public Try<Set<Tag>> findAllByIdIn(Collection<Long> ids) {
        return Try.of(() -> tagRepository.findTagsByIdIn(ids))
                .filter(tags -> !tags.isEmpty())
                .toTry(() -> new NotFound404Exception("Tags not found"))
                .onFailure(th -> log.error(th.getMessage(), th));
    }

    public Try<Tag> save(String name) {
        if (tagRepository.existsByName(name))
            return Try.failure(new AlreadyExists409Exception(ExceptionMsgPattern.ENTITY_ALREADY_EXISTS, Tag.class));
        return Try.of(() -> tagRepository.save(new Tag(name)))
                .onFailure(th -> log.warn("Cannot save refresh token", th));
    }

    public Try<List<Tag>> findAll() {
        return Try.of(tagRepository::findAll);
    }
}
