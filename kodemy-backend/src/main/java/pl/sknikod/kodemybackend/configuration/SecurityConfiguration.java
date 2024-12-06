package pl.sknikod.kodemybackend.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemycommons.exception.handler.ServletExceptionHandler;
import pl.sknikod.kodemycommons.security.JwtAuthorizationFilter;
import pl.sknikod.kodemycommons.security.JwtProvider;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true)
@AllArgsConstructor
//@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableAutoConfiguration(exclude = UserDetailsServiceAutoConfiguration.class)
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthorizationFilter jwtAuthorizationFilter,
            ServletExceptionHandler servletExceptionHandler
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(autz -> autz.anyRequest().permitAll())
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
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
    public JwtProvider jwtProvider(JwtProperties jwtProperties) {
        return new JwtProvider(jwtProperties);
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(JwtProvider jwtProvider) {
        return new JwtAuthorizationFilter(jwtProvider);
    }

    @Component
    @ConfigurationProperties(prefix = "app.security.jwt")
    public static class JwtProperties extends JwtProvider.Properties {
    }
}