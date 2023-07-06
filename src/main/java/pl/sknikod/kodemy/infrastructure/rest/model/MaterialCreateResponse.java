package pl.sknikod.kodemy.infrastructure.rest.model;

import lombok.Value;
import pl.sknikod.kodemy.infrastructure.model.entity.MaterialStatus;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Value
public class MaterialCreateResponse {
    Long id;
    String title;
    @Enumerated(EnumType.STRING)
    MaterialStatus status;
}

