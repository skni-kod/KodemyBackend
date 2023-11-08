package pl.sknikod.kodemyauth.infrastructure.user.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Role;
import pl.sknikod.kodemyauth.util.SwaggerResponse;

import javax.validation.Valid;

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
    @Operation(summary = "Search users")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.NotFoundCode404
    ResponseEntity<Page<UserInfoResponse>> searchForUser(
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(value = "sort", defaultValue = "createdDate") String sort,
            @RequestParam(value = "sort_direction", defaultValue = "DESC") Sort.Direction sortDirection,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Role.RoleName role,
            @RequestParam(required = false) String phrase
    );

}
