package pl.sknikod.kodemy.auth;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemy.user.UserProviderType;

import java.util.Map;

@RestController
@AllArgsConstructor
public class AuthController implements AuthControllerDefinition {
    private final AuthService authService;
    public static final String REDIRECT_URI_PARAMETER = "redirect_uri";

    @Override
    public void authorize(UserProviderType provider,String redirect_uri) {
    }

    @Override
    public void logout(String redirect_uri) {
    }

    @Override
    public ResponseEntity<UserProviderType[]> getProvidersList() {
        return ResponseEntity.ok(UserProviderType.values());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> getUserInfo(OAuth2AuthenticationToken auth) {
        return ResponseEntity.ok(authService.getUserInfo(auth));
    }
}