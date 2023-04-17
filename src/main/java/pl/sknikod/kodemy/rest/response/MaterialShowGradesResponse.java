package pl.sknikod.kodemy.rest.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Value;
import pl.sknikod.kodemy.grade.Grade;
import pl.sknikod.kodemy.material.MaterialStatus;
import pl.sknikod.kodemy.rest.BaseDetails;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Value
public class MaterialShowGradesResponse {
    Set<Grade> grades;
}
