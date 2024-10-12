package pl.sknikod.kodemybackend.infrastructure.common.lan;

import io.vavr.control.Try;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import pl.sknikod.kodemycommons.exception.InternalError500Exception;
import pl.sknikod.kodemycommons.network.LanRestTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class LanNetworkHandler {
    private final LanRestTemplate lanRestTemplate;
    private final String authRouteBaseUrl;

    private static final ParameterizedTypeReference<List<SimpleUserResponse>> USERS_LIST_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final String LOG_PROBLEM = "Problem in connection with the external service";

    public Try<Map<Long, String>> getUsers(Stream<Long> ids) {
        String queryString = ids
                .map(id -> "user=" + id)
                .collect(Collectors.joining("&"));

        return Try.of(() -> lanRestTemplate.exchange(
                        this.authRouteBaseUrl + "/api/users/brief?" + queryString,
                        HttpMethod.GET, null, USERS_LIST_TYPE
                ))
                .map(HttpEntity::getBody)
                .map(list -> list.stream().collect(Collectors.toMap(
                        LanNetworkHandler.SimpleUserResponse::getId,
                        LanNetworkHandler.SimpleUserResponse::getUsername
                )))
                .onFailure(th -> log.error(LOG_PROBLEM, th))
                .toTry(InternalError500Exception::new);
    }

    public Try<Map<Long, String>> getUsers(Collection<Long> ids) {
        return getUsers(ids.stream());
    }

    public Try<String> getUser(Long id) {
        return this.getUsers(Set.of(id))
                .map(users -> users.getOrDefault(id, null))
                .toTry(InternalError500Exception::new);
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class SimpleUserResponse {
        private Long id;
        private String username;
    }
}
