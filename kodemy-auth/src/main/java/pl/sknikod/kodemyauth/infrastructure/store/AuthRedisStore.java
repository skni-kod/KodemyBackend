package pl.sknikod.kodemyauth.infrastructure.store;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthRedisStore {
    private final StringRedisTemplate redisTemplate;

    private ValueOperations<String, String> opsForValue() {
        return redisTemplate.opsForValue();
    }

    public Try<String> findByKey(String key) {
        return Try.of(() -> opsForValue().get(key))
                .onFailure(th -> log.error("Problem with getting {} redis key value", key, th));
    }

    public void save(String key, String object) {
        Try.run(() -> opsForValue().set(key, object, Duration.ofMinutes(5)))
                .onFailure(th -> log.error("Problem with store {} redis key", key, th));
    }

    public void delete(String key) {
        Try.run(() -> redisTemplate.delete(key))
                .onFailure(th -> log.warn("Cannot delete redis {} key", key, th));
    }
}
