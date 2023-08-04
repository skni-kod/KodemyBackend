package pl.sknikod.kodemy.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.sknikod.kodemy.infrastructure.model.entity.RoleName;
import pl.sknikod.kodemy.infrastructure.rest.model.UserRoleChangeResponse;
import pl.sknikod.kodemy.util.SwaggerResponse;

import javax.validation.Valid;

@RequestMapping("/api/user")
@SwaggerResponse
@Tag(name = "User")
public interface UserControllerDefinition {

    @PatchMapping("/roles")
    @Operation(summary = "Change user's roles")
    @SwaggerResponse.UpdateRequest
    @SwaggerResponse.AuthRequest
    ResponseEntity<RoleName> updateRoles(@RequestBody @Valid UserRoleChangeResponse userRoleChangeResponse);

}
