package pl.sknikod.kodemy.type;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TypeService {
    private final TypeRepository typeRepository;
}
