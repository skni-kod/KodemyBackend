package pl.sknikod.kodemyauth.configuration;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.OAuth2AuthorizeRequestResolver;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.OAuth2SessionAuthRequestRepository;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.handler.OAuth2LoginFailureHandler;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.handler.OAuth2LoginSuccessHandler;
import pl.sknikod.kodemycommon.exception.handler.ServletExceptionHandler;
import pl.sknikod.kodemycommon.security.JwtAuthorizationFilter;
import pl.sknikod.kodemycommon.security.JwtProvider;

@TestConfiguration
public class TestSecurityConfig {
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return Mockito.mock(ClientRegistrationRepository.class);
    }

    @Bean
    public ServletExceptionHandler servletExceptionHandler() {
        return Mockito.mock(ServletExceptionHandler.class);
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(
    ) {
        return Mockito.mock(JwtAuthorizationFilter.class);
    }

    @Bean
    public OAuth2AuthorizeRequestResolver oAuth2AuthorizeRequestResolver() {
        return Mockito.mock(OAuth2AuthorizeRequestResolver.class);
    }

    @Bean
    public OAuth2SessionAuthRequestRepository oAuth2SessionAuthRequestRepository() {
        return Mockito.mock(OAuth2SessionAuthRequestRepository.class);
    }

    @Bean
    public OAuth2LoginSuccessHandler oAuth2SuccessProcessHandler(){
        return Mockito.mock(OAuth2LoginSuccessHandler.class);
    }

    @Bean
    public OAuth2LoginFailureHandler oAuth2FailureProcessHandler() {
        return Mockito.mock(OAuth2LoginFailureHandler.class);
    }
}