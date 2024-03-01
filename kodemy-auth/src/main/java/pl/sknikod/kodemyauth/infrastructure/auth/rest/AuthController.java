package pl.sknikod.kodemyauth.infrastructure.auth.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemyauth.infrastructure.auth.AuthService;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Provider;
import pl.sknikod.kodemyauth.util.JwtUtil;

import javax.servlet.http.HttpServletRequest;

@RestController
@AllArgsConstructor
public class AuthController implements AuthControllerDefinition {
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @Override
    public ResponseEntity<OAuth2LinksResponse> getLinks(String redirectUri, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(authService.getLinks(redirectUri, request));
    }

    @Override
    public ResponseEntity<Provider.ProviderType[]> getProvidersList() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(Provider.ProviderType.values());
    }

    @Override
    public ResponseEntity<AuthInfo> isAuthenticated() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(authService.isAuthenticated());
    }

    @Override
    public ResponseEntity<AuthResponse> getToken() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(authService.getSessionToken());
    }

    @Override
    public ResponseEntity<TokenInfo> validateToken(String bearer) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(new TokenInfo(jwtUtil.isTokenValid(bearer)));
    }
}