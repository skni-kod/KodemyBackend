package pl.sknikod.kodemy.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.sknikod.kodemy.infrastructure.rest.model.TechnologyAddRequest;
import pl.sknikod.kodemy.infrastructure.rest.model.TechnologyAddResponse;
import pl.sknikod.kodemy.util.SwaggerResponse;

@RequestMapping("/api/technologies")
@SwaggerResponse.SuccessCode
@SwaggerResponse.AuthRequest
@Tag(name = "Technology")
public interface TechnologyControllerDefinition {

    @PostMapping
    @Operation(summary = "Add new technology")
    @SwaggerResponse.CreateRequest
    ResponseEntity<TechnologyAddResponse> addTechnology(@RequestBody TechnologyAddRequest body);
}
