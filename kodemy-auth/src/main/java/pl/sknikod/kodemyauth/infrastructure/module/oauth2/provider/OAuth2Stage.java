package pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider;

import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.util.OAuth2RestOperations;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public abstract class OAuth2Stage implements OAuth2Provider.Stage {
    protected final OAuth2RestOperations oAuth2RestOperations;
    protected final Map<String, Object> attributes = new HashMap<>();

    public final Map<String, Object> retrieveAttributes(@NonNull OAuth2UserRequest userRequest) {
        attributes.clear();
        processBaseAttributes(userRequest);
        addSubProcesses(userRequest);
        return attributes;
    }

    private void processBaseAttributes(@NonNull OAuth2UserRequest userRequest) {
        Try.of(() -> this.oAuth2RestOperations.exchange(userRequest).getBody())
                .onSuccess(attrs -> log.info("Successfully retrieved {} user attributes", attrs.size()))
                .peek(this.attributes::putAll);
    }

    protected void addSubProcesses(@NonNull OAuth2UserRequest userRequest) {
        // placeholder to children
    }
}