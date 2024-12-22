package pl.sknikod.kodemyauth.infrastructure.module.oauth2.exchange;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExchangeEngine {
    private final List<ProviderExchange> providerExchanges;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public Try<ProviderUser> createProviderUser(String registrationId, Map<String, String> parameters) {
        return this.execute(registrationId, parameters.get("code"), providerExchanges.iterator());
    }

    private Try<ProviderUser> execute(String registrationId, String code, Iterator<ProviderExchange> iterator) {
        if (!iterator.hasNext()) {
            log.info("No processable provider for registration ID: {}", registrationId);
            return Try.failure(new OAuth2AuthorizationException(new OAuth2Error(
                    "no_processable_provider",
                    "No processable provider for registration ID",
                    null
            )));
        }
        ProviderExchange providerExchange = iterator.next();
        if (providerExchange.isApply(registrationId)) {
            log.info("Process {} provider flow", providerExchange.getClass().getSimpleName());
            return providerExchange.exchange(clientRegistrationRepository, code);
        }
        return execute(registrationId, code, iterator); // check another one
    }
}
