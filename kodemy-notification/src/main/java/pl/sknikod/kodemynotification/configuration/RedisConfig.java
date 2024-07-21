package pl.sknikod.kodemynotification.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemynotification.util.redis.RedisPoolManager;

@Configuration
public class RedisConfig {

    @Bean
    public RedisPoolManager redisConnectionManager(RedisProperties redisProperties) {
        return new RedisPoolManager(
                redisProperties.host,
                redisProperties.port,
                redisProperties.password,
                redisProperties.database,
                redisProperties.connectionTimeout,
                redisProperties.maxConnections,
                redisProperties.idleConnections,
                redisProperties.useSsl
        );
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @Component
    @ConfigurationProperties(prefix = "redis")
    public static class RedisProperties {
        private String host;
        private int port = 6379;
        private String password;
        private int database = 0;
        private int connectionTimeout = 30_000;
        private boolean useSsl = false;
        private int maxConnections = 10;
        private int idleConnections = 3;
    }
}
