package pl.sknikod.kodemy.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import pl.sknikod.kodemy.auth.AuthCookieAuthorizationRequestRepository;
import pl.sknikod.kodemy.auth.AuthEntryPoint;
import pl.sknikod.kodemy.auth.AuthService;
import pl.sknikod.kodemy.auth.handler.*;
import pl.sknikod.kodemy.util.filter.RefreshUserPrincipalFilter;

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
    private AppConfig.SecurityAuthProperties authProperties;
    private RefreshUserPrincipalFilter refreshUserPrincipalFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(refreshUserPrincipalFilter, FilterSecurityInterceptor.class)
                .authorizeHttpRequests(autz -> autz
                        .anyRequest().permitAll()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .oauth2Login(login -> login
                        .authorizationEndpoint()
                        .baseUri(authProperties.getLoginUri())
                        .authorizationRequestRepository(authCookieAuthorizationRequestRepository)
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
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(authAccessDeniedHandler)
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(
                                new AntPathRequestMatcher(authProperties.getLogoutUri(), HttpMethod.GET.name())
                        )
                        .addLogoutHandler(authLogoutHandler)
                        .logoutSuccessHandler(authLogoutSuccessHandler)
                        .invalidateHttpSession(false)
                        .deleteCookies(
                                "JSESSIONID",
                                AUTHORIZATION_REQUEST_COOKIE_NAME
                        )
                );
        return http.build();
    }
}