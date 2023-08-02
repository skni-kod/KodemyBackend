package pl.sknikod.kodemy.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.sknikod.kodemy.util.SwaggerResponse;


@RequestMapping("/api/user")
@SwaggerResponse
@Tag(name = "User")
public interface UserControllerDefinition {

    @PatchMapping("/{userId}/roles")
    @Operation(summary = "Change user's roles")
    ResponseEntity<?> updateRoles(@PathVariable String userId);

}
