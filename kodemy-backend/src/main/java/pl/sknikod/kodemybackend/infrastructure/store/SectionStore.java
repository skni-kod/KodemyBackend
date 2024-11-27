package pl.sknikod.kodemybackend.infrastructure.store;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.infrastructure.database.Section;
import pl.sknikod.kodemybackend.infrastructure.database.SectionRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SectionStore {
    private final SectionRepository sectionRepository;

    public Try<List<Section>> findAll() {
        return Try.of(sectionRepository::findAllWithFetchCategories);
    }
}
