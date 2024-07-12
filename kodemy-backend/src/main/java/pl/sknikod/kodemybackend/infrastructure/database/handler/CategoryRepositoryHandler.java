package pl.sknikod.kodemybackend.infrastructure.database.handler;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.exception.ExceptionPattern;
import pl.sknikod.kodemybackend.exception.structure.NotFoundException;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Category;
import pl.sknikod.kodemybackend.infrastructure.database.repository.CategoryRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryRepositoryHandler {
    private final CategoryRepository categoryRepository;

    public Try<Category> findById(Long id) {
        return Try.of(() -> categoryRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(ExceptionPattern.ENTITY_NOT_FOUND_BY_PARAM, Category.class, "id", id)))
                .onFailure(th -> log.error(th.getMessage(), th));
    }
}
