package pl.sknikod.kodemy.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.*;
import java.util.Set;

@Data
public class MaterialAddGradeRequest {
    @NotNull
    @Pattern(regexp = "^[0-5](\\.[05])?$")
    private String grade;

}
