package pl.sknikod.kodemy.infrastructure.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemy.infrastructure.model.user.UserProviderType;
import pl.sknikod.kodemy.infrastructure.rest.model.UserOAuth2MeResponse;

@RestController
@AllArgsConstructor
public class AuthController implements AuthControllerDefinition {
    public static final String REDIRECT_URI_PARAMETER = "redirect_uri";
    private final AuthService authService;

    @Override
    public void authorize(UserProviderType provider, String redirectUri) {
        /* DOCUMENTARY METHOD */
    }

    @Override
    public void logout(String redirectUri) {
        /* DOCUMENTARY METHOD */
    }

    @Override
    public ResponseEntity<UserProviderType[]> getProvidersList() {
        return ResponseEntity.ok(UserProviderType.values());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserOAuth2MeResponse> getUserInfo() {
        return ResponseEntity.ok(authService.getUserInfo());
    }
}