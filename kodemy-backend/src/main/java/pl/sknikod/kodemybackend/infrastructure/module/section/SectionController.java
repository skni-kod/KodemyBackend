package pl.sknikod.kodemybackend.infrastructure.module.section;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemybackend.infrastructure.module.section.model.SingleSectionResponse;
import pl.sknikod.kodemybackend.infrastructure.rest.SectionControllerDefinition;

import java.util.List;

@RestController
@AllArgsConstructor
public class SectionController implements SectionControllerDefinition {

    private final SectionUseCase sectionUseCase;

    @Override
    public ResponseEntity<List<SingleSectionResponse>> getAll() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(sectionUseCase.getAllSections());
    }
}
