package pl.sknikod.kodemysearch.infrastructure.module.material.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class MaterialAvgGradeEvent {
    Long id;
    double avgGrade;
}
