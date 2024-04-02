package pl.sknikod.kodemybackend.infrastructure.material;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;

@Component
@AllArgsConstructor
public class MaterialStatusUseCase {

    public Material.MaterialStatus update(Long materialId, Material.MaterialStatus newStatus) {
        return null;
    }
}
