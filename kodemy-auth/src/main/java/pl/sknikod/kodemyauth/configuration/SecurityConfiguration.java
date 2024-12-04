package pl.sknikod.kodemyauth.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.util.OAuth2Constant;
import pl.sknikod.kodemycommons.exception.handler.RestExceptionHandler;
import pl.sknikod.kodemycommons.exception.handler.ServletExceptionHandler;
import pl.sknikod.kodemycommons.security.JwtAuthorizationFilter;
import pl.sknikod.kodemycommons.security.JwtProvider;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true)
@RequiredArgsConstructor
@EnableAutoConfiguration(exclude = UserDetailsServiceAutoConfiguration.class)
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ServletExceptionHandler servletExceptionHandler
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(autz -> autz.anyRequest().permitAll())
                .formLogin(AbstractHttpConfigurer::disable)
                .oauth2Login(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(servletExceptionHandler::entryPoint)
                        .accessDeniedHandler(servletExceptionHandler::accessDenied)
                )
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public ServletExceptionHandler servletExceptionHandler(ObjectMapper objectMapper) {
        return new ServletExceptionHandler(objectMapper);
    }

    @Bean
    public RestExceptionHandler restExceptionHandler() {
        return new RestExceptionHandler();
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(
            OAuth2EndpointsProperties oAuth2EndpointsProperties,
            JwtProvider jwtProvider
    ) {
        final var permitPaths = List.of(
                oAuth2EndpointsProperties.authorize + OAuth2Constant.OAUTH2_PROVIDER_SUFFIX,
                oAuth2EndpointsProperties.callback + OAuth2Constant.OAUTH2_PROVIDER_SUFFIX
        );
        return new JwtAuthorizationFilter(permitPaths, jwtProvider);
    }

    @Bean
    public JwtProvider jwtProvider(JwtProperties jwtProperties) {
        return new JwtProvider(jwtProperties);
    }

    @Getter
    @Setter
    @Component
    @NoArgsConstructor
    @ConfigurationProperties(prefix = "app.security.oauth2.endpoints")
    public static class OAuth2EndpointsProperties {
        private String authorize;
        private String callback;
        private String error;
        private String redirect;
    }

    @Getter
    @Setter
    @Component
    @NoArgsConstructor
    @ConfigurationProperties(prefix = "app.security.roles")
    public static class RoleProperties {
        private String primary;
    }

    @Component
    @NoArgsConstructor
    @ConfigurationProperties(prefix = "app.security.jwt")
    public static class JwtProperties extends JwtProvider.Properties {
    }
}
