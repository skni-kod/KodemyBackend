package pl.sknikod.kodemy.technology;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TechnologyController {
    private final TechnologyService technologyService;
}