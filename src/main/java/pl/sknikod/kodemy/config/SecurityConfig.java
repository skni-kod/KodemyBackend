package pl.sknikod.kodemy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import pl.sknikod.kodemy.auth.AuthCookieAuthorizationRequestRepository;
import pl.sknikod.kodemy.auth.AuthEntryPoint;
import pl.sknikod.kodemy.auth.handler.AuthAccessDeniedHandler;
import pl.sknikod.kodemy.auth.handler.AuthAuthenticationFailureHandler;
import pl.sknikod.kodemy.auth.handler.AuthAuthenticationSuccessHandler;
import pl.sknikod.kodemy.auth.handler.AuthLogoutSuccessHandler;
import pl.sknikod.kodemy.auth.oauth2.OAuth2UserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig {
    @Autowired
    private AuthCookieAuthorizationRequestRepository authCookieAuthorizationRequestRepository;
    @Autowired
    private OAuth2UserService authUserService;
    @Autowired
    private AuthAuthenticationSuccessHandler authAuthenticationSuccessHandler;
    @Autowired
    private AuthAuthenticationFailureHandler authAuthenticationFailureHandler;
    @Autowired
    private AuthEntryPoint authEntryPoint;
    @Autowired
    private AuthAccessDeniedHandler authAccessDeniedHandler;
    @Autowired
    private AuthLogoutSuccessHandler authLogoutSuccessHandler;
    public final static String AUTH_REQUEST_BASE_URI = "/api/oauth2/authorize";
    @Value("${springdoc.api-docs.path}")
    private String apiDocsPath;
    @Value("${springdoc.swagger-ui.path}")
    private String swaggerUiPath;

    private String[] RequestGetWhitelist() {
        return new String[]{
                //Base
                "/",
                "/**/favicon.ico",
                "/api",
                "/error",
                //Auth
                "/api/oauth2/**",
                //OpenAPI
                "/api/swagger-ui/**",
                "/api/docs/swagger-config",
                apiDocsPath,
                swaggerUiPath,
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().disable()
                .authorizeRequests(autz -> autz
                        .antMatchers(HttpMethod.GET, RequestGetWhitelist()).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin().disable()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri(AUTH_REQUEST_BASE_URI)
                .authorizationRequestRepository(authCookieAuthorizationRequestRepository)
                .and()
                .redirectionEndpoint()
                .baseUri("/api/oauth2/callback/**")
                .and()
                .userInfoEndpoint()
                .userService(authUserService)
                .and()
                .successHandler(authAuthenticationSuccessHandler)
                .failureHandler(authAuthenticationFailureHandler)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authEntryPoint)
                .accessDeniedHandler(authAccessDeniedHandler)
                .and()
                .logout()
                .logoutUrl("api/logout")
                .logoutSuccessHandler(authLogoutSuccessHandler)
                .invalidateHttpSession(true)
                .deleteCookies(
                        "JSESSIONID",
                        AuthCookieAuthorizationRequestRepository.AUTHORIZATION_REQUEST_COOKIE_NAME
                );
        return http.build();
    }
}