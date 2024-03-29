package pl.sknikod.kodemy.infrastructure.section.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.sknikod.kodemy.util.SwaggerResponse;

import java.util.List;

@RequestMapping("api/sections")
@SwaggerResponse
@Tag(name = "Section")
public interface SectionControllerDefinition {

    @Operation(summary = "Show all Sections with Categories")
    @GetMapping
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.NotFoundCode404
    ResponseEntity<List<SingleSectionResponse>> getAll();
}
