package pl.sknikod.kodemy.type;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemy.rest.response.SingleTypeResponse;

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
