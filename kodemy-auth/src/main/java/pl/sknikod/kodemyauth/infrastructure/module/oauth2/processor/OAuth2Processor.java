package pl.sknikod.kodemyauth.infrastructure.module.oauth2.processor;

import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.util.OAuth2RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public abstract class OAuth2Processor {
    protected final OAuth2RestTemplate oAuth2RestTemplate;
    private final Map<String, Object> attributes = new HashMap<>();

    public abstract OAuth2ProcessorResult process(OAuth2UserRequest userRequest);

    protected Map<String, Object> getAttributes(@NonNull OAuth2UserRequest userRequest) {
        Try.of(() -> this.oAuth2RestTemplate.exchange(userRequest).getBody())
                .onSuccess(attrs -> log.info("Successfully retrieved {} user attributes", attrs.size()))
                .peek(m -> {
                    this.attributes.put("registration_id", userRequest.getClientRegistration().getRegistrationId());
                    this.attributes.putAll(m);
                });
        return attributes;
    }
}
