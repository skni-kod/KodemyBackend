package pl.sknikod.kodemy.infrastructure.user.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sknikod.kodemy.infrastructure.common.entity.RoleName;
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

    @GetMapping("/{userId}")
    @Operation(summary = "Show information about user")
    @SwaggerResponse.ReadRequest
    ResponseEntity<UserInfoResponse> getUserInfo(@PathVariable Long userId);

    @GetMapping("/me")
    @Operation(summary = "Show information about logged user")
    @SwaggerResponse.ReadRequest
    ResponseEntity<UserInfoResponse> getCurrentUserInfo();

}
