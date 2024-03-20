package pl.sknikod.kodemyauth.configuration;

import io.vavr.control.Option;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.client.RestTemplate;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Role;
import pl.sknikod.kodemyauth.infrastructure.oauth2.AuthorizationRequestRepositoryImpl;
import pl.sknikod.kodemyauth.infrastructure.oauth2.OAuth2Controller;
import pl.sknikod.kodemyauth.infrastructure.oauth2.OAuth2Service;
import pl.sknikod.kodemyauth.infrastructure.oauth2.filter.OAuth2RedirectFilter;
import pl.sknikod.kodemyauth.infrastructure.oauth2.handler.InvalidationLogoutHandler;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
@AllArgsConstructor
@DependsOn(value = {
        "securityConfig.SecurityProperties.AuthProperties",
        "securityConfig.SecurityProperties.CorsProperties",
})
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            SecurityProperties.AuthProperties authProperties,
            AuthorizationRequestRepositoryImpl authorizationRequestRepository,
            OAuth2Controller oAuth2Controller,
            OAuth2Service oAuth2Service,
            OAuth2RedirectFilter oAuth2RedirectFilter,
            InvalidationLogoutHandler invalidationLogoutHandler
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable).cors()
                .and()
                .authorizeHttpRequests(autz -> autz.anyRequest().permitAll())
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .formLogin(AbstractHttpConfigurer::disable)
                .oauth2Login(login -> login
                        .authorizationEndpoint(endpoint -> {
                            endpoint.baseUri(authProperties.uri.login);
                            endpoint.authorizationRequestRepository(authorizationRequestRepository);
                        })
                        .redirectionEndpoint(endpoint -> endpoint.baseUri(authProperties.uri.callback))
                        .authorizedClientService(oAuth2Service)
                        .successHandler(oAuth2Controller::successLoginResponse)
                        .failureHandler(oAuth2Controller::failureLoginResponse)
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(oAuth2Controller::unauthorizedResponse)
                        .accessDeniedHandler(oAuth2Controller::forbiddenResponse)
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher(authProperties.uri.logout, "GET"))
                        .addLogoutHandler(invalidationLogoutHandler)
                        .invalidateHttpSession(false)
                        .deleteCookies(authProperties.cookieKey.currentSession)
                        .logoutSuccessHandler(oAuth2Controller::successLogoutResponse)
                )
                .addFilterBefore(oAuth2RedirectFilter, OAuth2AuthorizationRequestRedirectFilter.class);
        return http.build();
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Principal::getName);
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SecurityProperties {

        @Configuration
        @Data
        @ConfigurationProperties(prefix = "app.security.auth")
        public static class AuthProperties {
            private UriProperties uri;
            private CookieKeyProperties cookieKey;
            private TokenProperties token;
            private String sessionEncryptPassword;

            @Getter
            @Setter
            public static class UriProperties {
                private String login;
                private String callback;
                private String logout;
            }

            @Getter
            @Setter
            public static class CookieKeyProperties {
                private String currentSession;
                private String accessSession;
                private String refreshSession;
            }

            @Getter
            @Setter
            public static class TokenProperties {
                private TokenDetails access;
                private TokenDetails refresh;

                @Getter
                @Setter
                public static class TokenDetails {
                    private int expirationInMin;
                    private String secretKey;
                }
            }
        }

        @Configuration
        @Data
        @ConfigurationProperties(prefix = "app.security.role")
        public static class RoleProperties {
            private String defaultRole;
            private Map<String, Set<String>> privileges = new LinkedHashMap<>();

            public Set<SimpleGrantedAuthority> getPrivileges(Role.RoleName role) {
                return Option
                        .of(privileges.get(role.toString()))
                        .map(Collection::stream)
                        .map(stream -> stream.map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toSet()))
                        .getOrElse(Collections::emptySet);
            }
        }

        @Configuration
        @Data
        @ConfigurationProperties(prefix = "app.security.cors")
        public static class CorsProperties {
            private String[] allowedUris;
        }
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}