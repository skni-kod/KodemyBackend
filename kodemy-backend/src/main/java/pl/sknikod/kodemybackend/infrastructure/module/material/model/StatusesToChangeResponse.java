package pl.sknikod.kodemybackend.infrastructure.module.material.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import pl.sknikod.kodemybackend.infrastructure.database.Material;

import java.util.List;

public record StatusesToChangeResponse(
        @Enumerated(EnumType.STRING) List<Material.MaterialStatus> statuses) {
}
