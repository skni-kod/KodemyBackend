package pl.sknikod.kodemy.infrastructure.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemy.infrastructure.model.entity.UserProviderType;

@RestController
@AllArgsConstructor
public class AuthController implements AuthControllerDefinition {
    public static final String REDIRECT_URI_PARAMETER = "redirect_uri";

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
        return ResponseEntity.status(HttpStatus.OK).body(UserProviderType.values());
    }
}