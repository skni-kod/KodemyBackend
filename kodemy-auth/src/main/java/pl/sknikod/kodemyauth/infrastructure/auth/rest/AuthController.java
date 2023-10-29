package pl.sknikod.kodemyauth.infrastructure.auth.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemyauth.configuration.AppConfig;
import pl.sknikod.kodemyauth.infrastructure.auth.AuthService;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Provider;
import pl.sknikod.kodemyauth.util.JwtUtil;

import javax.servlet.http.HttpServletRequest;

@RestController
@AllArgsConstructor
public class AuthController implements AuthControllerDefinition {
    public static final String REDIRECT_URI_PARAMETER = "redirect_uri";
    private final AuthService authService;
    private final AppConfig.AuthProperties authProperties;
    private final JwtUtil jwtUtil;

    @Override
    public ResponseEntity<String> authorize(Provider.ProviderType provider, String redirectUri, HttpServletRequest request) {
        var link = authService.getLink(request, uriBuilder -> uriBuilder
                .path(authProperties.getLoginUri())
                .path("/" + provider), redirectUri
        );
        return ResponseEntity.status(HttpStatus.OK)
                .body(link);
    }

    @Override
    public ResponseEntity<String> logout(String redirectUri, HttpServletRequest request) {
        String link = authService.getLink(request, uriBuilder -> uriBuilder
                .path(authProperties.getLogoutUri()), redirectUri);
        return ResponseEntity.status(HttpStatus.OK)
                .body(link);
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