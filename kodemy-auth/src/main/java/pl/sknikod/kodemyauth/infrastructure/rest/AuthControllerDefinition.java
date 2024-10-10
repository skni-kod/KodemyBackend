package pl.sknikod.kodemyauth.infrastructure.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.sknikod.kodemyauth.infrastructure.module.auth.model.RefreshTokensResponse;
import pl.sknikod.kodemycommon.doc.SwaggerResponse;

import java.util.UUID;

@RequestMapping("/api/auth")
@Tag(name = "Auth")
@SwaggerResponse
@SwaggerResponse.SuccessCode200
public interface AuthControllerDefinition {
    @GetMapping("/refresh")
    ResponseEntity<RefreshTokensResponse> validateToken(
            @RequestParam UUID refresh,
            @RequestParam UUID bearerJti
    );
}