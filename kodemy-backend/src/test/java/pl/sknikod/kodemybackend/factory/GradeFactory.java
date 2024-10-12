package pl.sknikod.kodemybackend.factory;

import pl.sknikod.kodemybackend.infrastructure.database.Grade;
import pl.sknikod.kodemybackend.infrastructure.database.Material;

public class GradeFactory {
    private GradeFactory() {
    }

    public static Grade grade(Long id, String grade, Long materialId) {
        var entity = new Grade();
        entity.setId(id);
        entity.setValue(Double.valueOf(grade));
        entity.setMaterial(MaterialFactory.material(materialId, Material.MaterialStatus.APPROVED));
        return entity;
    }
}
