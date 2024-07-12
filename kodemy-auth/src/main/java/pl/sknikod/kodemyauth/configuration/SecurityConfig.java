package pl.sknikod.kodemyauth.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.infrastructure.database.handler.RefreshTokenRepositoryHandler;
import pl.sknikod.kodemyauth.infrastructure.module.auth.handler.LogoutSuccessHandler;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.OAuth2AuthorizeRequestResolver;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.OAuth2Service;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.OAuth2SessionAuthRequestRepository;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.handler.OAuth2LoginFailureHandler;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.handler.OAuth2LoginSuccessHandler;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.util.OAuth2Constant;
import pl.sknikod.kodemyauth.util.auth.JwtAuthorizationFilter;
import pl.sknikod.kodemyauth.util.auth.JwtService;
import pl.sknikod.kodemyauth.util.auth.handler.AccessControlExceptionHandler;
import pl.sknikod.kodemyauth.util.data.AuditorAwareAdapter;
import pl.sknikod.kodemyauth.util.route.RouteRedirectStrategy;

import java.util.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true)
@AllArgsConstructor
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthorizationFilter jwtAuthorizationFilter,
            OAuth2AuthorizeRequestResolver oAuth2AuthorizeRequestResolver,
            OAuth2SessionAuthRequestRepository oAuth2SessionAuthRequestRepository,
            OAuth2PathProperties oAuth2PathProperties,
            OAuth2Service oAuth2Service,
            OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
            OAuth2LoginFailureHandler oAuth2LoginFailureHandler,
            AccessControlExceptionHandler accessControlExceptionHandler,
            LogoutSuccessHandler logoutSuccessHandler
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(autz -> autz.anyRequest().permitAll())
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .oauth2Login(login -> login
                        .authorizationEndpoint(config -> config
                                .authorizationRequestResolver(oAuth2AuthorizeRequestResolver)
                                .authorizationRequestRepository(oAuth2SessionAuthRequestRepository)
                        )
                        .redirectionEndpoint(config -> config.baseUri((
                                oAuth2PathProperties.callback + OAuth2Constant.OAUTH2_PROVIDER_SUFFIX
                        )))
                        .userInfoEndpoint(config -> config.userService(oAuth2Service))
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(accessControlExceptionHandler::entryPoint)
                        .accessDeniedHandler(accessControlExceptionHandler::accessDenied)
                )
                .logout(config -> config.logoutSuccessHandler(logoutSuccessHandler))
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

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

    @Getter
    @Setter
    @Component
    @NoArgsConstructor
    @ConfigurationProperties(prefix = "spring.security.oauth2.path")
    public static class OAuth2PathProperties {
        private String authorize;
        private String callback;
        private String error;
    }

    @Getter
    @Setter
    @Component
    @NoArgsConstructor
    @ConfigurationProperties(prefix = "app.security.roles")
    public static class RoleProperties {
        private String primary;
        private LinkedHashMap<String, Set<SimpleGrantedAuthority>> authorities = new LinkedHashMap<>();

        public Set<SimpleGrantedAuthority> getAuthorities(String role) {
            return authorities.getOrDefault(role, Collections.emptySet());
        }
    }
}
