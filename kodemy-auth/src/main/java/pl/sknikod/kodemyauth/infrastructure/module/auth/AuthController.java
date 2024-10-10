package pl.sknikod.kodemyauth.infrastructure.module.auth;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemyauth.infrastructure.module.auth.model.RefreshTokensResponse;
import pl.sknikod.kodemyauth.infrastructure.rest.AuthControllerDefinition;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class AuthController implements AuthControllerDefinition {
    private final RefreshTokensService refreshTokensService;

    @Override
    public ResponseEntity<RefreshTokensResponse> validateToken(UUID refresh, UUID bearerJti) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(refreshTokensService.refresh(refresh, bearerJti));
    }
}