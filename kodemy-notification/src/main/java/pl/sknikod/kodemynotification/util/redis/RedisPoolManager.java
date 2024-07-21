package pl.sknikod.kodemynotification.util.redis;

import io.vavr.control.Try;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.sknikod.kodemynotification.configuration.RedisConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.util.Pool;

@Slf4j
public class RedisPoolManager {
    private final Pool<Jedis> jedisPool;

    public RedisPoolManager(
            String host, int port, String password, int database, int timeout,
            int maxConnections, int idleConnections,
            boolean useSsl
    ) {
        this.jedisPool = Try.of(() -> new JedisPool(
                        createPoolConfig(maxConnections, idleConnections),
                        host, port, timeout, password, database, useSsl
                ))
                .onSuccess(obj -> log.info("Jedis Pool created: ({}:{})", host, port))
                .getOrElseThrow(th -> new RuntimeException(th));
    }

    private JedisPoolConfig createPoolConfig(int maxConnections, int idleConnections) {
        final var poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxConnections);
        poolConfig.setMaxIdle(idleConnections);
        return poolConfig;
    }

    public Jedis getResource() {
        return jedisPool.getResource();
    }
}
