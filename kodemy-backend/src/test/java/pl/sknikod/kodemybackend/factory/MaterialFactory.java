package pl.sknikod.kodemybackend.factory;

import pl.sknikod.kodemybackend.infrastructure.database.Material;

public class MaterialFactory {
    public static Material material(){
        var material = new Material();
        material.setId(1L);
        material.setStatus(Material.MaterialStatus.PENDING);
        return material;
    }

    public static Material material(Long id, Material.MaterialStatus status){
        var material = new Material();
        material.setId(id);
        material.setStatus(status);
        return material;
    }
}
