package pl.sknikod.kodemy.base;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class BaseController {

    @GetMapping
    public ResponseEntity<?> index() {
        return ResponseEntity.ok("Platforma działa poprawnie ;)");
    }
}