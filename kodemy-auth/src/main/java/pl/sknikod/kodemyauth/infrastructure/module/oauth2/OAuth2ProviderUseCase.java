package pl.sknikod.kodemyauth.infrastructure.module.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.OAuth2Provider;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OAuth2ProviderUseCase {
    private final List<OAuth2Provider> oAuth2Providers;

    public String[] getProviders() {
        return oAuth2Providers
                .stream()
                .map(OAuth2Provider::getRegistrationId)
                .toArray(String[]::new);
    }
}
