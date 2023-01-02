package pl.sknikod.kodemy.section;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class SectionController {
    private final SectionService sectionService;
}
