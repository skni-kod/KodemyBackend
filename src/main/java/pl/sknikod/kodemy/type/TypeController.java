package pl.sknikod.kodemy.type;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TypeController {
    private final TypeService typeService;
}
