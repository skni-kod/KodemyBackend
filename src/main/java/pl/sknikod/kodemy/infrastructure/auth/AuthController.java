package pl.sknikod.kodemy.infrastructure.auth;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemy.configuration.AppConfig;
import pl.sknikod.kodemy.infrastructure.common.entity.UserProviderType;

import javax.servlet.http.HttpServletRequest;

@RestController
@AllArgsConstructor
public class AuthController implements AuthControllerDefinition {
    public static final String REDIRECT_URI_PARAMETER = "redirect_uri";
    private final AuthService authService;
    private final AppConfig.SecurityAuthProperties authProperties;

    @Override
    public ResponseEntity<String> authorize(UserProviderType provider, String redirectUri, HttpServletRequest request) {
        String link = authService.getLink(request, uriComponentsBuilder -> uriComponentsBuilder
                .path(authProperties.getLoginUri()).path("/" + provider), redirectUri);
        return ResponseEntity.status(HttpStatus.OK).body(link);
    }

    @Override
    public ResponseEntity<String> logout(String redirectUri, HttpServletRequest request) {
        String link = authService.getLink(request, uriComponentsBuilder -> uriComponentsBuilder
                .path(authProperties.getLogoutUri()), redirectUri);
        return ResponseEntity.status(HttpStatus.OK).body(link);
    }

    @Override
    public ResponseEntity<UserProviderType[]> getProvidersList() {
        return ResponseEntity.status(HttpStatus.OK).body(authService.getProvidersList());
    }
}