package pl.sknikod.kodemybackend.infrastructure.dao;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.infrastructure.database.Category;
import pl.sknikod.kodemybackend.infrastructure.database.CategoryRepository;
import pl.sknikod.kodemycommon.exception.NotFound404Exception;
import pl.sknikod.kodemycommon.exception.content.ExceptionMsgPattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryDao {
    private final CategoryRepository categoryRepository;

    public Try<Category> findById(Long id) {
        return Try.of(() -> categoryRepository.findById(id)
                        .orElseThrow(() -> new NotFound404Exception(ExceptionMsgPattern.ENTITY_NOT_FOUND_BY_PARAM, Category.class, "id", id)))
                .onFailure(th -> log.error(th.getMessage(), th));
    }
}
