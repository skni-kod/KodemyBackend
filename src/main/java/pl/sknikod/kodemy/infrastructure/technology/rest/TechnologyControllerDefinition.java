package pl.sknikod.kodemy.infrastructure.technology.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.sknikod.kodemy.util.SwaggerResponse;

import java.util.List;

@RequestMapping("/api/technologies")
@SwaggerResponse
@SwaggerResponse.SuccessCode
@SwaggerResponse.UnauthorizedCode
@SwaggerResponse.ForbiddenCode
@Tag(name = "Technology")
public interface TechnologyControllerDefinition {

    @PostMapping
    @Operation(summary = "Add new technology")
    @SwaggerResponse.CreatedCode
    @SwaggerResponse.BadRequestCode
    ResponseEntity<TechnologyAddResponse> addTechnology(@RequestBody TechnologyAddRequest body);

    @GetMapping
    @Operation(summary = "Show all technologies")
    @SwaggerResponse.SuccessCode
    ResponseEntity<List<TechnologyAddResponse>> showTechnologies();
}
