package pl.sknikod.kodemyauth.util.data;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;
import pl.sknikod.kodemyauth.util.auth.JwtService;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;

public class LanRestTemplate extends RestTemplate {
    private static final String AUTHORITY = "LAN_NETWORK_DATABUS";

    public LanRestTemplate(
            int connectTimeoutMs,
            int readTimeoutMs,
            JwtService jwtService
    ) {
        super();
        this.setRequestFactory(createClientHttpRequestFactory(
                connectTimeoutMs, readTimeoutMs
        ));
        this.getInterceptors().add(new DelegationTokenRequestInterceptor(jwtService));
    }

    private SimpleClientHttpRequestFactory createClientHttpRequestFactory(int connectTimeoutMs, int readTimeoutMs) {
        final var clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(connectTimeoutMs);
        clientHttpRequestFactory.setReadTimeout(readTimeoutMs);
        return clientHttpRequestFactory;
    }

    private record DelegationTokenRequestInterceptor(JwtService jwtService) implements ClientHttpRequestInterceptor {
        private static DelegationToken delegationToken = null;

        @Nullable
        private DelegationToken generateDelegationToken() {
            var token = this.jwtService.generateDelegationToken("pl.sknikod.kodemy", AUTHORITY);
            if (token == null) return null;
            return new DelegationToken(token.value(), token.expiration());
        }

        private boolean isTokenExpired(DelegationToken token) {
            return false;
        }

        @Override
        @NonNull
        public ClientHttpResponse intercept(
                @NonNull HttpRequest request,
                @NonNull byte[] body,
                @NonNull ClientHttpRequestExecution execution
        ) throws IOException {
            if (delegationToken == null || isTokenExpired(delegationToken))
                delegationToken = generateDelegationToken();
            if (delegationToken != null)
                request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + delegationToken);
            return execution.execute(request, body);
        }

        private record DelegationToken(
                @NonNull String value,
                @NonNull Date expiredDate
        ) {
        }
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated() and hasAuthority('" + AUTHORITY + "')")
    public @interface PreAuthorize {
    }
}
