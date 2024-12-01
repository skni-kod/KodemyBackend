package pl.sknikod.kodemyauth.infrastructure.module.oauth2.exchange;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProviderEngine {
    private final List<ProviderExchangeFlow> providerExchangeFlows;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public Optional<ProviderUser> createProviderUser(String registrationId, Map<String, String> parameters) {
        return this.execute(registrationId, parameters.get("code"), providerExchangeFlows.iterator());
    }

    private Optional<ProviderUser> execute(String registrationId, String code, Iterator<ProviderExchangeFlow> iterator) {
        if (!iterator.hasNext()) {
            log.info("No processable provider for registration ID: {}", registrationId);
            return Optional.empty();
        }
        ProviderExchangeFlow providerExchangeFlow = iterator.next();
        if (providerExchangeFlow.isApply(registrationId)) {
            log.info("Process {} provider flow", providerExchangeFlow.getClass().getSimpleName());
            return Optional.of(providerExchangeFlow.exchange(clientRegistrationRepository, code));
        }
        return execute(registrationId, code, iterator); // check another one
    }
}
