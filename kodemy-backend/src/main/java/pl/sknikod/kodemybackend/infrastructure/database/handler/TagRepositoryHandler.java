package pl.sknikod.kodemybackend.infrastructure.database.handler;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.exception.ExceptionPattern;
import pl.sknikod.kodemybackend.exception.structure.AlreadyExistsException;
import pl.sknikod.kodemybackend.exception.structure.NotFoundException;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Tag;
import pl.sknikod.kodemybackend.infrastructure.database.repository.TagRepository;
import pl.sknikod.kodemybackend.infrastructure.module.tag.model.TagAddRequest;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TagRepositoryHandler {
    private final TagRepository tagRepository;

    public Try<Set<Tag>> findAllByIdIn(Collection<Long> ids) {
        return Try.of(() -> tagRepository.findTagsByIdIn(ids))
                .filter(tags -> !tags.isEmpty())
                .toTry(() -> new NotFoundException("Tags not found"))
                .onFailure(th -> log.error(th.getMessage(), th));
    }

    public Try<Tag> save(String name) {
        if (tagRepository.existsByName(name))
            return Try.failure(new AlreadyExistsException(ExceptionPattern.ENTITY_ALREADY_EXISTS, Tag.class));
        return Try.of(() -> tagRepository.save(new Tag(name)))
                .onFailure(th -> log.warn("Cannot save refresh token", th));
    }

    public Try<List<Tag>> findAll() {
        return Try.of(tagRepository::findAll);
    }
}
