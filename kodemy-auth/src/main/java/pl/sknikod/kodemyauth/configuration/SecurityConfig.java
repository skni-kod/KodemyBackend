package pl.sknikod.kodemyauth.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import pl.sknikod.kodemyauth.exception.ExceptionRestGenericMessage;
import pl.sknikod.kodemyauth.infrastructure.auth.AuthService;
import pl.sknikod.kodemyauth.infrastructure.auth.AuthorizationRequestRepositoryImpl;
import pl.sknikod.kodemyauth.infrastructure.auth.handler.AuthAuthenticationFailureHandler;
import pl.sknikod.kodemyauth.infrastructure.auth.handler.AuthAuthenticationSuccessHandler;
import pl.sknikod.kodemyauth.infrastructure.auth.handler.AuthLogoutHandler;
import pl.sknikod.kodemyauth.infrastructure.auth.handler.AuthLogoutSuccessHandler;

import java.security.Principal;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@AllArgsConstructor
public class SecurityConfig {
    private final AuthorizationRequestRepositoryImpl authorizationRequestRepository;
    private final AuthService authService;
    private final AuthAuthenticationSuccessHandler authAuthenticationSuccessHandler;
    private final AuthAuthenticationFailureHandler authAuthenticationFailureHandler;
    private final AuthLogoutHandler authLogoutHandler;
    private final AuthLogoutSuccessHandler authLogoutSuccessHandler;
    private final AppConfig.AuthProperties authProperties;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(autz -> autz
                        .anyRequest().permitAll()
                )
                .formLogin().disable()
                .oauth2Login(login -> login
                        .authorizationEndpoint()
                        .baseUri(authProperties.getLoginUri())
                        .authorizationRequestRepository(authorizationRequestRepository)
                        .and()
                        .redirectionEndpoint()
                        .baseUri(authProperties.getCallbackUri())
                        .and()
                        .userInfoEndpoint()
                        .userService(authService)
                        .and()
                        .successHandler(authAuthenticationSuccessHandler)
                        .failureHandler(authAuthenticationFailureHandler)
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((req, res, e) ->
                                ExceptionRestGenericMessage.writeBodyResponseForHandler(res, objectMapper, e, HttpStatus.UNAUTHORIZED)
                        )
                        .accessDeniedHandler((req, res, e) ->
                                ExceptionRestGenericMessage.writeBodyResponseForHandler(res, objectMapper, e, HttpStatus.FORBIDDEN)
                        )
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(
                                new AntPathRequestMatcher(authProperties.getLogoutUri(), HttpMethod.GET.name())
                        )
                        .addLogoutHandler(authLogoutHandler)
                        .logoutSuccessHandler(authLogoutSuccessHandler)
                        .invalidateHttpSession(false)
                        .deleteCookies(
                                "JSESSIONID"
                        )
                )
                .cors();
        return http.build();
    }

    @Bean
    public org.springframework.data.domain.AuditorAware<String> auditorAware() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Principal::getName);
    }
}