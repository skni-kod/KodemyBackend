package pl.sknikod.kodemy.config;

import io.vavr.control.Option;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@Import({
        SecurityConfig.class,
        AuditConfig.class,
        RabbitMQConfig.class
})
public class AppConfig {
    @Component
    @Data
    @ConfigurationProperties(prefix = "kodemy.security.auth")
    public static class SecurityAuthProperties {
        private String loginUri;
        private String callbackUri;
        private String logoutUri;
    }

    @Component
    @Data
    @ConfigurationProperties(prefix = "kodemy.security.role")
    public static class SecurityRoleProperties {
        private String defaultRole;
        private Map<String, Set<String>> privileges = new LinkedHashMap<>();

        public Set<SimpleGrantedAuthority> getPrivileges(String role) {
            return Option
                    .of(privileges.get(role))
                    .map(Collection::stream)
                    .map(stream -> stream.map(SimpleGrantedAuthority::new).collect(Collectors.toSet()))
                    .getOrElse(Collections::emptySet);
        }
    }
}
