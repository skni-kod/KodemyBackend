package pl.sknikod.kodemy.infrastructure.user.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sknikod.kodemy.infrastructure.common.entity.RoleName;
import pl.sknikod.kodemy.util.SwaggerResponse;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/api/users")
@SwaggerResponse
@SwaggerResponse.SuccessCode
@SwaggerResponse.UnauthorizedCode
@SwaggerResponse.ForbiddenCode
@Tag(name = "User")
public interface UserControllerDefinition {

    @PatchMapping("/{userId}/roles")
    @Operation(summary = "Change user's roles")
    @SwaggerResponse.BadRequestCode
    @SwaggerResponse.NotFoundCode
    void updateRoles(@PathVariable Long userId, @RequestBody @Valid RoleName roleName);

    @GetMapping("/{userId}")
    @Operation(summary = "Show information about user")
    @SwaggerResponse.SuccessCode
    @SwaggerResponse.NotFoundCode
    ResponseEntity<UserInfoResponse> getUserInfo(@PathVariable Long userId);

    @GetMapping("/me")
    @Operation(summary = "Show information about logged user")
    @SwaggerResponse.SuccessCode
    @SwaggerResponse.NotFoundCode
    ResponseEntity<UserInfoResponse> getCurrentUserInfo();

    @GetMapping("/find")
    @Operation(summary = "Show list of users based on phrase")
    @SwaggerResponse.SuccessCode
    @SwaggerResponse.NotFoundCode
    ResponseEntity<List<UserInfoResponse>> searchForUser(@RequestParam String phrase);

}
