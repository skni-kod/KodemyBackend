package pl.sknikod.kodemy.grade;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GradeService {
    private final GradeRepository gradeRepository;
}
