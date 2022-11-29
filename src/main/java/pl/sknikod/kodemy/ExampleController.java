package pl.sknikod.kodemy;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ExampleController {

    @GetMapping
    public ResponseEntity<?> index() {
        return ResponseEntity.ok("Platforma działa poprawnie ;)");
    }
}
