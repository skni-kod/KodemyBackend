package pl.sknikod.kodemybackend.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.sknikod.kodemybackend.util.data.AuditorAwareAdapter;
import pl.sknikod.kodemycommon.exception.handler.ServletExceptionHandler;
import pl.sknikod.kodemycommon.security.JwtAuthorizationFilter;
import pl.sknikod.kodemycommon.security.JwtProvider;
import pl.sknikod.kodemycommon.security.configuration.JwtConfiguration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true)
@AllArgsConstructor
@Import({JwtConfiguration.class})
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthorizationFilter jwtAuthorizationFilter,
            ServletExceptionHandler servletExceptionHandler
            //LogoutSuccessHandler logoutSuccessHandler
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(autz -> autz.anyRequest().permitAll())
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(Customizer.withDefaults())
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(servletExceptionHandler::entryPoint)
                        .accessDeniedHandler(servletExceptionHandler::accessDenied)
                )
                //.logout(config -> config.logoutSuccessHandler(logoutSuccessHandler))
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public AuditorAware<?> auditorAware() {
        return new AuditorAwareAdapter();
    }

    @Bean
    public ServletExceptionHandler servletExceptionHandler(ObjectMapper objectMapper) {
        return new ServletExceptionHandler(objectMapper);
    }

    @Bean
    public JwtConfiguration.JwtProperties jwtProperties() {
        return new JwtConfiguration.JwtProperties();
    }

    @Bean
    public JwtProvider jwtProvider(JwtConfiguration.JwtProperties jwtProperties){
        return new JwtProvider(jwtProperties);
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(JwtConfiguration.JwtProperties jwtProperties) {
        return new JwtAuthorizationFilter(jwtProperties);
    }
}