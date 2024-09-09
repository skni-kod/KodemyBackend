package pl.sknikod.kodemybackend.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.sknikod.kodemybackend.infrastructure.module.type.model.SingleTypeResponse;
import pl.sknikod.kodemycommon.doc.SwaggerResponse;

import java.util.List;

@RequestMapping("/api/types")
@SwaggerResponse
@Tag(name = "Type")
public interface TypeControllerDefinition {
    @Operation(summary = "Show all Types")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.NotFoundCode404
    @GetMapping
    ResponseEntity<List<SingleTypeResponse>> getAll();
}
