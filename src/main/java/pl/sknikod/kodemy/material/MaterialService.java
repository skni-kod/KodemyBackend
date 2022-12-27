package pl.sknikod.kodemy.material;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MaterialService {
    private final MaterialRepository materialRepository;
}