package pl.sknikod.kodemy.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import pl.sknikod.kodemy.auth.AuthCookieAuthorizationRequestRepository;
import pl.sknikod.kodemy.auth.AuthEntryPoint;
import pl.sknikod.kodemy.auth.AuthService;
import pl.sknikod.kodemy.auth.handler.*;

import static pl.sknikod.kodemy.auth.AuthCookieAuthorizationRequestRepository.AUTHORIZATION_REQUEST_COOKIE_NAME;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
@AllArgsConstructor
public class SecurityConfig {
    private AuthCookieAuthorizationRequestRepository authCookieAuthorizationRequestRepository;
    private AuthService authService;
    private AuthAuthenticationSuccessHandler authAuthenticationSuccessHandler;
    private AuthAuthenticationFailureHandler authAuthenticationFailureHandler;
    private AuthEntryPoint authEntryPoint;
    private AuthAccessDeniedHandler authAccessDeniedHandler;
    private AuthLogoutHandler authLogoutHandler;
    private AuthLogoutSuccessHandler authLogoutSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable()
                .authorizeRequests(autz -> autz
                        .anyRequest().permitAll()
                )
                .formLogin().disable()
                .oauth2Login(login -> login
                        .authorizationEndpoint()
                        .baseUri("/api/oauth2/authorize")
                        .authorizationRequestRepository(authCookieAuthorizationRequestRepository)
                        .and()
                        .redirectionEndpoint()
                        .baseUri("/api/oauth2/callback/**")
                        .and()
                        .userInfoEndpoint()
                        .userService(authService)
                        .and()
                        .successHandler(authAuthenticationSuccessHandler)
                        .failureHandler(authAuthenticationFailureHandler)
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(authAccessDeniedHandler)
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(
                                new AntPathRequestMatcher("/api/oauth2/logout", HttpMethod.GET.name())
                        )
                        .addLogoutHandler(authLogoutHandler)
                        .logoutSuccessHandler(authLogoutSuccessHandler)
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies(
                                "JSESSIONID",
                                AUTHORIZATION_REQUEST_COOKIE_NAME
                        )
                );
        return http.build();
    }
}