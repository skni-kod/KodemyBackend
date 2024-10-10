package pl.sknikod.kodemyauth.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.sknikod.kodemycommon.doc.SwaggerResponse;

@RequestMapping("/api/roles")
@SwaggerResponse
@SwaggerResponse.SuccessCode200
@SwaggerResponse.UnauthorizedCode401
@SwaggerResponse.ForbiddenCode403
@Tag(name = "Role")
public interface RoleControllerDefinition {

    @GetMapping
    @Operation(summary = "Show available roles")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.NotFoundCode404
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<String[]> getRoles();

}
