package pl.sknikod.kodemy.infrastructure.rest.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class MaterialAddGradeRequest {
    @NotNull
    @Pattern(regexp = "^[0-5](\\.[05])?$")
    private String grade;

}
