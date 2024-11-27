package pl.sknikod.kodemybackend.infrastructure.store;

import io.vavr.control.Try;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemycommons.exception.InternalError500Exception;
import pl.sknikod.kodemycommons.network.LanRestTemplate;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class UserStore {
    private final LanRestTemplate lanRestTemplate;
    private final String authRouteBaseUrl;
    private static final ParameterizedTypeReference<List<User>> USERS_LIST_TYPE;
    private static final String LOG_PROBLEM = "Problem in connection with the external service";

    static {
        USERS_LIST_TYPE = new ParameterizedTypeReference<>() {
        };
    }

    public UserStore(LanRestTemplate lanRestTemplate, @Value("${network.route.auth}") String authRouteBaseUrl) {
        this.lanRestTemplate = lanRestTemplate;
        this.authRouteBaseUrl = authRouteBaseUrl;
    }

    private String createURI(Stream<Long> ids) {
        String queryString = ids.map(id -> "user=" + id).collect(Collectors.joining("&"));
        return authRouteBaseUrl + "/api/users/brief?" + queryString;
    }

    public Try<List<User>> findUsersById(Stream<Long> ids) {
        return Try.of(() -> lanRestTemplate.exchange(createURI(ids), HttpMethod.GET, null, USERS_LIST_TYPE))
                .map(HttpEntity::getBody)
                .onFailure(th -> log.error(LOG_PROBLEM, th))
                .toTry(InternalError500Exception::new);
    }

    public Try<List<User>> findUsersById(Collection<Long> ids) {
        return findUsersById(ids.stream());
    }

    public Try<User> findById(Long id) {
        return Try.of(() -> lanRestTemplate.exchange(createURI(Stream.of(id)), HttpMethod.GET, null, USERS_LIST_TYPE))
                .map(HttpEntity::getBody)
                .mapTry(users -> users.get(0))
                .onFailure(th -> log.error(LOG_PROBLEM, th))
                .toTry(InternalError500Exception::new);
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class User {
        private Long id;
        private String username;
    }
}
