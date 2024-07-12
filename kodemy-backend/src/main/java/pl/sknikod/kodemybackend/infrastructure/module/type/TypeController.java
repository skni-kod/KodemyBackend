package pl.sknikod.kodemybackend.infrastructure.module.type;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemybackend.infrastructure.module.type.model.SingleTypeResponse;
import pl.sknikod.kodemybackend.infrastructure.rest.TypeControllerDefinition;

import java.util.List;

@RestController
@AllArgsConstructor
public class TypeController implements TypeControllerDefinition {

    private final TypeUseCase typeUseCase;

    @Override
    public ResponseEntity<List<SingleTypeResponse>> getAll() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(typeUseCase.getAllTypes());
    }
}
