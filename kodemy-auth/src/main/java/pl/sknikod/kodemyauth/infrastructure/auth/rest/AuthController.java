package pl.sknikod.kodemyauth.infrastructure.auth.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemyauth.infrastructure.auth.AuthService;

import javax.servlet.http.HttpServletRequest;

@RestController
@AllArgsConstructor
public class AuthController implements AuthControllerDefinition {
    private final AuthService authService;

    @Override
    public ResponseEntity<OAuth2LinksResponse> getLinks(String redirectUri, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(authService.getLinks(redirectUri, request));
    }
}