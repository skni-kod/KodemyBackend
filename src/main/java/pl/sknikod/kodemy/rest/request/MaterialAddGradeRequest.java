package pl.sknikod.kodemy.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.*;
import java.util.Set;

@Data
public class MaterialAddGradeRequest {
    @NotNull
    @Pattern(regexp = "^(1|1\\.5|2|2\\.5|3|3\\.5|4|4\\.5|5)$")
    @Schema(example = "Grade of the material")
    private String grade;

}
