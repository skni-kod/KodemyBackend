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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class LanNetworkHandler {
    private final LanRestTemplate lanRestTemplate;
    private final String authRouteBaseUrl;

    private static final ParameterizedTypeReference<List<SimpleUserResponse>> USERS_LIST_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final String LOG_PROBLEM = "Problem in connection with the external service";

    public Try<List<SimpleUserResponse>> getUsers(Set<Long> ids) {
        String queryString = ids.stream()
                .map(id -> "user=" + id)
                .collect(Collectors.joining("&"));

        return Try.of(() -> lanRestTemplate.exchange(
                        this.authRouteBaseUrl + "/api/users/simple?" + queryString,
                        HttpMethod.GET, null, USERS_LIST_TYPE
                ))
                .map(HttpEntity::getBody)
                .onFailure(th -> log.error(LOG_PROBLEM, th))
                .toTry(InternalError500Exception::new);
    }

    public Try<SimpleUserResponse> getUser(Long id) {
        return this.getUsers(Set.of(id))
                .filter(v -> !v.isEmpty())
                .toTry(InternalError500Exception::new)
                .map(v -> v.get(0));
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class SimpleUserResponse {
        private Long id;
        private String username;
    }
}
