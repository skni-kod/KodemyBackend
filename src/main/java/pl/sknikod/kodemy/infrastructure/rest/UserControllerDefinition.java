package pl.sknikod.kodemy.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sknikod.kodemy.infrastructure.model.entity.RoleName;
import pl.sknikod.kodemy.util.SwaggerResponse;

import javax.validation.Valid;

@RequestMapping("/api/user")
@SwaggerResponse
@Tag(name = "User")
public interface UserControllerDefinition {

    @PatchMapping("/roles/{userId}")
    @Operation(summary = "Change user's roles")
    @SwaggerResponse.UpdateRequest
    @SwaggerResponse.AuthRequest
    ResponseEntity<RoleName> updateRoles(@PathVariable Long userId, @RequestBody @Valid RoleName roleName);

}
