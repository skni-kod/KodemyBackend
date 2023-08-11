package pl.sknikod.kodemy.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.sknikod.kodemy.infrastructure.model.entity.RoleName;
import pl.sknikod.kodemy.util.SwaggerResponse;

@RequestMapping("/api/roles")
@SwaggerResponse.SuccessCode
@SwaggerResponse.AuthRequest
@Tag(name = "Role")
public interface RoleControllerDefinition {

    @GetMapping
    @Operation(summary = "Show available roles")
    @SwaggerResponse.ReadRequest
    ResponseEntity<RoleName[]> getRoles();

}
