package pl.sknikod.kodemybackend.infrastructure.database.handler;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.exception.ExceptionPattern;
import pl.sknikod.kodemybackend.exception.structure.NotFoundException;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Type;
import pl.sknikod.kodemybackend.infrastructure.database.repository.TypeRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TypeRepositoryHandler {
    private final TypeRepository typeRepository;

    public Try<Type> findById(Long id) {
        return Try.of(() -> typeRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(ExceptionPattern.ENTITY_NOT_FOUND_BY_PARAM,
                                Type.class.getSimpleName(), "id", id)))
                .onFailure(th -> log.error(th.getMessage(), th));
    }

    public Try<List<Type>> findAll() {
        return Try.of(typeRepository::findAll);
    }
}
