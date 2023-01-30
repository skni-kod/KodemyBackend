package pl.sknikod.kodemy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import pl.sknikod.kodemy.auth.AuthSessionRequestRepository;
import pl.sknikod.kodemy.auth.oauth2.OAuth2UserService;
import pl.sknikod.kodemy.auth.handler.AuthAuthorizationFailureHandler;
import pl.sknikod.kodemy.auth.handler.AuthAuthorizationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private AuthSessionRequestRepository authSessionRequestRepository;
    @Autowired
    private OAuth2UserService authUserService;
    @Autowired
    private AuthAuthorizationSuccessHandler authAuthorizationSuccessHandler;
    @Autowired
    private AuthAuthorizationFailureHandler authAuthorizationFailureHandler;

    private String[] RequestGetWhitelist() {
        return new String[]{
                //Base
                "/",
                "/api",
                "/error",
                //Auth
                "/api/oauth2/**"
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable();
        http
                .authorizeRequests(autz -> autz
                        .antMatchers(HttpMethod.GET, RequestGetWhitelist()).permitAll()
                        .anyRequest().authenticated()
                );
        http
                .formLogin()
                .disable()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/api/oauth2/authorize")
                .authorizationRequestRepository(authSessionRequestRepository)
                .and()
                .redirectionEndpoint()
                .baseUri("/api/oauth2/callback/**")
                .and()
                .userInfoEndpoint()
                .userService(authUserService)
                .and()
                .successHandler(authAuthorizationSuccessHandler)
                .failureHandler(authAuthorizationFailureHandler);
        return http.build();
    }
}
