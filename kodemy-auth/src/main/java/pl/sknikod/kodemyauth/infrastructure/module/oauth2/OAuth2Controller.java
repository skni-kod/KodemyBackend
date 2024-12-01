package pl.sknikod.kodemyauth.infrastructure.module.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.exchange.Registration;
import pl.sknikod.kodemyauth.infrastructure.rest.OAuth2ControllerDefinition;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class OAuth2Controller implements OAuth2ControllerDefinition {
    private final OAuth2ProvidersService oAuth2ProvidersService;
    private final OAuth2AuthorizeService oAuth2AuthorizeService;

    @Override
    public ResponseEntity<List<OAuth2ProvidersService.ProviderResponse>> getProvidersList() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(oAuth2ProvidersService.getProviders());
    }

    @Override
    public ResponseEntity<?> authorize(Registration registration, Map<String, String> parameters) {
        oAuth2AuthorizeService.authorize(registration, parameters);
        return ResponseEntity.status(HttpStatus.OK).body("Hello");
    }
}
