package pl.sknikod.kodemyauth.infrastructure.module.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemyauth.infrastructure.rest.OAuth2ControllerDefinition;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OAuth2Controller implements OAuth2ControllerDefinition {
    private final OAuth2ProviderService oAuth2ProviderService;

    @Override
    public ResponseEntity<List<OAuth2ProviderService.ProviderResponse>> getProvidersList() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(oAuth2ProviderService.getProviders());
    }
}
