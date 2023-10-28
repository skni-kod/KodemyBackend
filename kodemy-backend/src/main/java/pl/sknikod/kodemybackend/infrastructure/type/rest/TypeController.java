package pl.sknikod.kodemybackend.infrastructure.type.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemybackend.infrastructure.type.TypeService;

import java.util.List;

@RestController
@AllArgsConstructor
public class TypeController implements TypeControllerDefinition {

    private final TypeService typeService;

    @Override
    public ResponseEntity<List<SingleTypeResponse>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(typeService.getAllTypes());
    }
}
