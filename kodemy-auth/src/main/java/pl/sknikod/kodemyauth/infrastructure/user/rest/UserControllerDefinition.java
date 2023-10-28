package pl.sknikod.kodemyauth.infrastructure.user.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Role;
import pl.sknikod.kodemyauth.util.SwaggerResponse;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/api/users")
@SwaggerResponse
@SwaggerResponse.SuccessCode200
@SwaggerResponse.UnauthorizedCode401
@SwaggerResponse.ForbiddenCode403
@Tag(name = "User")
public interface UserControllerDefinition {

    @PatchMapping("/{userId}/roles")
    @Operation(summary = "Change user's roles")
    @SwaggerResponse.BadRequestCode400
    @SwaggerResponse.NotFoundCode404
    void updateRoles(@PathVariable Long userId, @RequestBody @Valid Role.RoleName roleName);

    @GetMapping("/{userId}")
    @Operation(summary = "Show information about user")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.NotFoundCode404
    ResponseEntity<UserInfoResponse> getUserInfo(@PathVariable Long userId);

    @GetMapping("/me")
    @Operation(summary = "Show information about logged user")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.NotFoundCode404
    ResponseEntity<UserInfoResponse> getCurrentUserInfo();

    @GetMapping
    @Operation(summary = "Show list of users based on phrase")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.NotFoundCode404
    ResponseEntity<List<UserInfoResponse>> searchForUser(@RequestParam String phrase);

}
