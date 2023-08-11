package pl.sknikod.kodemy.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import pl.sknikod.kodemy.infrastructure.model.entity.RoleName;
import pl.sknikod.kodemy.util.SwaggerResponse;

import javax.validation.Valid;

@RequestMapping("/api/users")
@SwaggerResponse.SuccessCode
@SwaggerResponse.AuthRequest
@Tag(name = "User")
public interface UserControllerDefinition {

    @PatchMapping("/{userId}/roles")
    @Operation(summary = "Change user's roles")
    @SwaggerResponse.UpdateRequest
    void updateRoles(@PathVariable Long userId, @RequestBody @Valid RoleName roleName);

}
