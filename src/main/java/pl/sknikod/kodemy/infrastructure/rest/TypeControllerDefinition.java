package pl.sknikod.kodemy.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.sknikod.kodemy.infrastructure.rest.model.SingleTypeResponse;
import pl.sknikod.kodemy.util.SwaggerResponse;

import java.util.List;

@RequestMapping("/api/types")
@SwaggerResponse
@Tag(name = "Type")
public interface TypeControllerDefinition {
    @Operation(summary = "Show all Types")
    @GetMapping
    @SwaggerResponse.ReadRequest
    ResponseEntity<List<SingleTypeResponse>> getAll();
}
