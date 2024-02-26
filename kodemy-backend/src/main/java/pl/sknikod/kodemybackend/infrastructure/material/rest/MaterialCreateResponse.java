package pl.sknikod.kodemybackend.infrastructure.material.rest;

import lombok.Value;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Value
public class MaterialCreateResponse {
    Long id;
    String title;
    @Enumerated(EnumType.STRING)
    Material.MaterialStatus status;
}

