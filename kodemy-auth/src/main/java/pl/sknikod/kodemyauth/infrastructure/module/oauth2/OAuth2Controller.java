package pl.sknikod.kodemyauth.infrastructure.module.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemyauth.infrastructure.rest.OAuth2ControllerDefinition;

@RestController
@RequiredArgsConstructor
public class OAuth2Controller implements OAuth2ControllerDefinition {
    private final OAuth2ProviderUseCase oAuth2ProviderUseCase;

    @Override
    public ResponseEntity<String[]> getProvidersList() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(oAuth2ProviderUseCase.getProviders());
    }
}
