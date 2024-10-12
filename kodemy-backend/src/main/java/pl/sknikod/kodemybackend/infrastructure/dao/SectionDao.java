package pl.sknikod.kodemybackend.infrastructure.dao;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.infrastructure.database.Section;
import pl.sknikod.kodemybackend.infrastructure.database.SectionRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SectionDao {
    private final SectionRepository sectionRepository;

    public Try<List<Section>> findAll() {
        return Try.of(sectionRepository::findAllWithFetchCategories);
    }
}
