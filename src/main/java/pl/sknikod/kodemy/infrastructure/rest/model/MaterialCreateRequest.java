package pl.sknikod.kodemy.infrastructure.rest.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.Set;

@Data
@AllArgsConstructor
public class MaterialCreateRequest {
    @NotNull
    @Schema(example = "Title of the material")
    private String title;

    @Schema(example = "Description of the material")
    private String description;

    @Pattern(regexp = "^https?://.*", message = "Link must start with http:// or https://")
    @Schema(example = "https://www.example.com/material/java-programming")
    private String link;

    @NotNull
    @Positive(message = "Type ID must be > 0")
    private Long typeId;

    @NotNull
    @Positive(message = "Category ID must be > 0")
    private Long categoryId;

    private Set<@NotNull @Positive(message = "Technology ID must be > 0") Long> technologiesIds;
}
