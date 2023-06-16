package pl.sknikod.kodemy.infrastructure.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemy.infrastructure.rest.model.SingleSectionResponse;

import java.util.List;

@RestController
@AllArgsConstructor
public class SectionController implements SectionControllerDefinition {

    private final SectionService sectionService;

    @Override
    public ResponseEntity<List<SingleSectionResponse>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(sectionService.getAllSections());
    }
}
