package pl.sknikod.kodemy.section;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SectionService {
    private final SectionRepository sectionRepository;
}
