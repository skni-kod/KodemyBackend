package pl.sknikod.kodemyauth.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import pl.sknikod.kodemyauth.infrastructure.database.handler.RefreshTokenRepositoryHandler;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.OAuth2AuthorizeRequestResolver;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.OAuth2SessionAuthRequestRepository;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.handler.OAuth2LoginFailureHandler;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.handler.OAuth2LoginSuccessHandler;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.util.OAuth2Constant;
import pl.sknikod.kodemyauth.util.auth.JwtAuthorizationFilter;
import pl.sknikod.kodemyauth.util.auth.JwtService;
import pl.sknikod.kodemyauth.util.auth.handler.AccessControlExceptionHandler;
import pl.sknikod.kodemyauth.util.data.AuditorAwareAdapter;
import pl.sknikod.kodemyauth.util.route.RouteRedirectStrategy;

import java.util.List;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class SecurityBeanConfig {
    @Bean
    public AuditorAware<?> auditorAware() {
        return new AuditorAwareAdapter();
    }

    @Bean
    public AccessControlExceptionHandler accessControlExceptionHandler(ObjectMapper objectMapper) {
        return new AccessControlExceptionHandler(objectMapper);
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(
            SecurityConfig.OAuth2PathProperties oAuth2PathProperties,
            JwtService jwtService
    ){
        final var permitPaths = List.of(
                oAuth2PathProperties.getCallback() + OAuth2Constant.OAUTH2_PROVIDER_SUFFIX
        );
        return new JwtAuthorizationFilter(permitPaths, jwtService);
    }

    @Bean
    public OAuth2AuthorizeRequestResolver oAuth2AuthorizeRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository,
            SecurityConfig.OAuth2PathProperties oAuth2PathProperties
    ) {
        return new OAuth2AuthorizeRequestResolver(
                clientRegistrationRepository, oAuth2PathProperties.getAuthorize());
    }

    @Bean
    public OAuth2SessionAuthRequestRepository oAuth2SessionAuthRequestRepository(
            ClientRegistrationRepository clientRegistrationRepository,
            SecurityConfig.OAuth2PathProperties oAuth2PathProperties
    ) {
        return new OAuth2SessionAuthRequestRepository(clientRegistrationRepository,
                oAuth2PathProperties.getCallback() + OAuth2Constant.OAUTH2_PROVIDER_SUFFIX);
    }

    @Bean
    public OAuth2LoginSuccessHandler oAuth2SuccessProcessHandler(
            RouteRedirectStrategy routeRedirectStrategy,
            JwtService jwtService,
            @Value("${network.routes.front}") String frontRouteBaseUrl,
            RefreshTokenRepositoryHandler refreshTokenRepositoryHandler
    ) {
        final var handler = new OAuth2LoginSuccessHandler(jwtService, frontRouteBaseUrl, refreshTokenRepositoryHandler);
        handler.setRedirectStrategy(routeRedirectStrategy);
        return handler;
    }

    @Bean
    public OAuth2LoginFailureHandler oAuth2FailureProcessHandler(
            RouteRedirectStrategy routeRedirectStrategy,
            @Value("${network.routes.front}") String frontRouteBaseUrl
    ) {
        final var handler = new OAuth2LoginFailureHandler(frontRouteBaseUrl);
        handler.setRedirectStrategy(routeRedirectStrategy);
        return handler;
    }
}
