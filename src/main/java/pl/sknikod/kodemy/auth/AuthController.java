package pl.sknikod.kodemy.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import pl.sknikod.kodemy.user.UserProviderType;
import pl.sknikod.kodemy.util.BaseApiResponses;

import java.util.Map;

@RestController
@RequestMapping("/api/oauth2")
@Tag(name = "OAuth2")
@AllArgsConstructor
@BaseApiResponses
public class AuthController {
    public final static String DEFAULT_REDIRECT_URL_AFTER_LOGIN = "http://localhost:8181/api/oauth2/me";
    public final static String DEFAULT_REDIRECT_URL_AFTER_LOGOUT = "http://localhost:8181/api/me";
    private final AuthService authService;

    @GetMapping("/authorize/{provider}")
    @Operation(summary = "Sign in via OAuth2 (ONLY WORK OUTSIDE SWAGGER)")
    public void authorize(@PathVariable UserProviderType provider, @RequestParam String redirect_uri) {
    }

    @GetMapping("/logout")
    @Operation(summary = "Logout")
    public void logout(@RequestParam String redirect_uri) {
    }

    @GetMapping("/providers")
    @Operation(summary = "Get all OAuth2 providers")
    public ResponseEntity<?> getProvidersList() {
        return ResponseEntity.ok(UserProviderType.values());
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get information about logged user")
    public ResponseEntity<Map<String, String>> getUserInfo(OAuth2AuthenticationToken auth) {
        return ResponseEntity.ok(authService.getUserInfo(auth));
    }
}