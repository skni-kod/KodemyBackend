package pl.sknikod.kodemy.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.sknikod.kodemy.infrastructure.model.entity.RoleName;
import pl.sknikod.kodemy.util.SwaggerResponse;

@RequestMapping("/api/user")
@SwaggerResponse
@Tag(name = "User")
public interface UserControllerDefinition {

    @PatchMapping("/{userId}/roles/{roleId}")
    @Operation(summary = "Change user's roles")
    @SwaggerResponse.UpdateRequest
    ResponseEntity<RoleName> updateRoles(@PathVariable Long userId , @PathVariable Long roleId);

}
