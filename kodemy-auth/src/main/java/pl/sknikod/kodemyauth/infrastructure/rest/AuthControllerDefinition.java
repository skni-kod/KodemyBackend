package pl.sknikod.kodemyauth.infrastructure.rest;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.sknikod.kodemyauth.infrastructure.module.auth.model.AuthInfoResponse;
import pl.sknikod.kodemyauth.infrastructure.module.auth.model.RefreshTokensResponse;
import pl.sknikod.kodemycommons.doc.SwaggerResponse;

import java.util.UUID;

@RequestMapping("/api/auth")
@Tag(name = "Auth")
@SwaggerResponse
@SwaggerResponse.SuccessCode200
public interface AuthControllerDefinition {
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<AuthInfoResponse> getAuth();

    @GetMapping("/refresh")
    ResponseEntity<RefreshTokensResponse> validateToken(
            @RequestParam UUID refresh,
            @RequestParam UUID bearerJti
    );

    @GetMapping("/access_token")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearer")
    ResponseEntity<String> getAccessToken(HttpServletRequest request);

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<Void> logout(HttpServletRequest request);

}