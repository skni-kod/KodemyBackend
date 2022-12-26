package pl.sknikod.kodemy.grade;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class GradeController {
    private final GradeService gradeService;
}
