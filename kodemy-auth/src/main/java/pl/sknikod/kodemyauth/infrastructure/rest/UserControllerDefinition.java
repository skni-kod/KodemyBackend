package pl.sknikod.kodemyauth.infrastructure.rest;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.sknikod.kodemyauth.infrastructure.module.user.model.FilterSearchParams;
import pl.sknikod.kodemyauth.infrastructure.module.user.model.SimpleUserResponse;
import pl.sknikod.kodemyauth.infrastructure.module.user.model.UserInfoResponse;
import pl.sknikod.kodemyauth.infrastructure.module.user.model.UserSortField;
import pl.sknikod.kodemycommons.doc.SwaggerResponse;
import pl.sknikod.kodemycommons.network.LanRestTemplate;

import java.util.List;
import java.util.Set;

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
    @PreAuthorize("isAuthenticated() and hasAuthority('CAN_ASSIGN_ROLES')")
    ResponseEntity<Void> updateRoles(
            @PathVariable Long userId,
            @RequestBody @Valid @RequestParam(defaultValue = "ROLE_USER") String roleName
    );

    @GetMapping("/{userId}")
    @Operation(summary = "Show information about user")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.NotFoundCode404
    @PreAuthorize("isAuthenticated() and hasAuthority('CAN_GET_USER_INFO')")
    ResponseEntity<UserInfoResponse> getUserInfo(@PathVariable Long userId);

    @GetMapping("/me")
    @Operation(summary = "Show information about logged user")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.NotFoundCode404
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<UserInfoResponse> getCurrentUserInfo();

    @GetMapping
    @Operation(summary = "Search users")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.NotFoundCode404
    ResponseEntity<Page<UserInfoResponse>> searchUsers(
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(value = "sort", defaultValue = "ID") UserSortField sortField,
            @RequestParam(value = "sort_direction", defaultValue = "ASC") Sort.Direction sortDirection,
            @Parameter(description = "{\"username\": \"username\", \"email\": \"email@example.com\", \"role\": \"ROLE_USER\"}")
            @RequestParam(value = "filters", required = false) FilterSearchParams filterSearchParams
    );

    @Hidden
    @GetMapping("/brief")
    @LanRestTemplate.PreAuthorize
    ResponseEntity<List<SimpleUserResponse>> getUsersBrief(
            @RequestParam("user") Set<Long> ids
    );
}
