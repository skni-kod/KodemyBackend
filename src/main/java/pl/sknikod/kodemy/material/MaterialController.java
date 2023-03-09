package pl.sknikod.kodemy.material;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class MaterialController {
    private final MaterialService materialService;
}