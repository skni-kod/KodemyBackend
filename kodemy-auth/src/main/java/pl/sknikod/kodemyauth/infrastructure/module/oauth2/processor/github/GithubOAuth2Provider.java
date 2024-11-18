package pl.sknikod.kodemyauth.infrastructure.module.oauth2.processor.github;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.processor.OAuth2Provider;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.processor.OAuth2ProcessorResult;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.util.OAuth2RestTemplate;

@Component
@Slf4j
@RequiredArgsConstructor
public class GithubOAuth2Provider implements OAuth2Provider {
    private static final String REGISTRATION_ID = "github";
    private final OAuth2RestTemplate oAuth2RestTemplate;

    @Override
    public String getRegistrationId() {
        return REGISTRATION_ID;
    }

    @Override
    public boolean isApply(String registrationId) {
        return getRegistrationId().equals(registrationId);
    }

    @Override
    public OAuth2ProcessorResult retrieve(OAuth2UserRequest userRequest) {
        return new GithubOAuth2Processor(oAuth2RestTemplate).process(userRequest);
    }
}
