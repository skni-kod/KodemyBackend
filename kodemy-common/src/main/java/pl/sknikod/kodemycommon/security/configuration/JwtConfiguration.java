package pl.sknikod.kodemycommon.security.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class JwtConfiguration {
    @Getter
    @Setter
    @Component
    @NoArgsConstructor
    @ConfigurationProperties(prefix = "jwt")
    public static class JwtProperties {
        private String secretKey;
        private Bearer bearer = new Bearer();
        private Delegation delegation = new Delegation();

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Bearer {
            private Integer expirationMin = 15;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Delegation {
            private Integer expirationMin = 60;
        }
    }
}
