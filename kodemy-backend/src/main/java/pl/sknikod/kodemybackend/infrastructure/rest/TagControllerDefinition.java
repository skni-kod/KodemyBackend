package pl.sknikod.kodemybackend.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.sknikod.kodemybackend.infrastructure.module.tag.model.TagAddRequest;
import pl.sknikod.kodemybackend.infrastructure.module.tag.model.TagAddResponse;
import pl.sknikod.kodemycommon.doc.SwaggerResponse;

import java.util.List;

@RequestMapping("/api/tags")
@SwaggerResponse
@SwaggerResponse.SuccessCode200
@SwaggerResponse.UnauthorizedCode401
@SwaggerResponse.ForbiddenCode403
@Tag(name = "Tag")
public interface TagControllerDefinition {

    @PostMapping
    @Operation(summary = "Add new tag")
    @SwaggerResponse.CreatedCode201
    @SwaggerResponse.BadRequestCode400
    ResponseEntity<TagAddResponse> addTag(@RequestBody @Valid TagAddRequest body);

    @GetMapping
    @Operation(summary = "Show all tags")
    @SwaggerResponse.SuccessCode200
    ResponseEntity<List<TagAddResponse>> showTags();
}
