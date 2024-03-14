package pl.sknikod.kodemyauth.infrastructure.oauth2.filter;

import lombok.SneakyThrows;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.configuration.SecurityConfig;
import pl.sknikod.kodemyauth.infrastructure.oauth2.OAuth2Controller;

import java.lang.reflect.Field;

@Component
public class OAuth2RedirectFilter extends OAuth2AuthorizationRequestRedirectFilter {

    @SneakyThrows
    public OAuth2RedirectFilter(
            SecurityConfig.SecurityProperties.AuthProperties authProperties,
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2Controller oAuth2Controller,
            AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository
    ) {
        super(new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository,
                authProperties.getUri().getLogin()
        ));
        super.setAuthorizationRequestRepository(authorizationRequestRepository);
        Field field = OAuth2AuthorizationRequestRedirectFilter.class
                .getDeclaredField("authorizationRedirectStrategy");
        field.setAccessible(true);
        field.set(this, (RedirectStrategy) oAuth2Controller::redirectStrategyResponse);
    }
}
