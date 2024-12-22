package pl.sknikod.kodemyauth.infrastructure.module.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemyauth.infrastructure.module.auth.model.AuthInfoResponse;
import pl.sknikod.kodemyauth.infrastructure.module.auth.model.GetAuthService;
import pl.sknikod.kodemyauth.infrastructure.module.auth.model.RefreshTokensResponse;
import pl.sknikod.kodemyauth.infrastructure.rest.AuthControllerDefinition;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class AuthController implements AuthControllerDefinition {
    private final AccessTokenService accessTokenService;
    private final RefreshTokensService refreshTokensService;
    private final LogoutService logoutService;
    private final GetAuthService getAuthService;

    @Override
    public ResponseEntity<AuthInfoResponse> getAuth() {
        return ResponseEntity.status(HttpStatus.OK).body(getAuthService.getAuth());
    }

    @Override
    public ResponseEntity<RefreshTokensResponse> validateToken(UUID refresh, UUID bearerJti) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(refreshTokensService.refresh(refresh, bearerJti));
    }

    @Override
    public ResponseEntity<String> getAccessToken(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(accessTokenService.getAccessToken(request.getHeader(HttpHeaders.AUTHORIZATION)));
    }

    @Override
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        logoutService.logout(request.getHeader(HttpHeaders.AUTHORIZATION));
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}