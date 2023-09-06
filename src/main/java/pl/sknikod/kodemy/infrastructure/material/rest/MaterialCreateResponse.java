package pl.sknikod.kodemy.infrastructure.material.rest;

import lombok.Value;
import pl.sknikod.kodemy.infrastructure.common.entity.MaterialStatus;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Value
public class MaterialCreateResponse {
    Long id;
    String title;
    @Enumerated(EnumType.STRING)
    MaterialStatus status;
}

