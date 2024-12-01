package pl.sknikod.kodemyauth.infrastructure.module.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.engine.Registration;
import pl.sknikod.kodemyauth.infrastructure.rest.OAuth2ControllerDefinition;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class OAuth2Controller implements OAuth2ControllerDefinition {
    private final OAuth2GetProvidersService oAuth2GetProvidersService;
    private final OAuth2AuthorizeService oAuth2AuthorizeService;
    private static final String ACCESS_TOKEN_COOKIE = "AUTH_CONTEXT";
    private static final String REFRESH_TOKEN_COOKIE = "AUTH_PERSIST";

    @Override
    public ResponseEntity<List<OAuth2GetProvidersService.ProviderResponse>> getProvidersList() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(oAuth2GetProvidersService.getProviders());
    }

    @Override
    public ResponseEntity<Void> authorize(
            Registration registration, Map<String, String> parameters) {
        final var tokens = oAuth2AuthorizeService.authorize(registration, parameters);
        return ResponseEntity.status(HttpStatus.OK)
                .header(ACCESS_TOKEN_COOKIE, tokens.getAccessToken())
                .header(REFRESH_TOKEN_COOKIE, tokens.getRefreshToken())
                .build();
    }
}
