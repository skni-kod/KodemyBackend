package pl.sknikod.kodemybackend.infrastructure.database.handler;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Section;
import pl.sknikod.kodemybackend.infrastructure.database.repository.SectionRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SectionRepositoryHandler {
    private final SectionRepository sectionRepository;

    public Try<List<Section>> findAll() {
        return Try.of(sectionRepository::findAllWithFetchCategories);
    }
}
