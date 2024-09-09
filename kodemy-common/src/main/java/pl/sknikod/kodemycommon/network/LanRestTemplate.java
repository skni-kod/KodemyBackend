package pl.sknikod.kodemycommon.network;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;
import pl.sknikod.kodemycommon.security.JwtProvider;

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
            JwtProvider jwtProvider
    ) {
        super();
        this.setRequestFactory(createClientHttpRequestFactory(
                connectTimeoutMs, readTimeoutMs
        ));
        this.getInterceptors().add(new DelegationTokenRequestInterceptor(jwtProvider));
    }

    private SimpleClientHttpRequestFactory createClientHttpRequestFactory(int connectTimeoutMs, int readTimeoutMs) {
        final var clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(connectTimeoutMs);
        clientHttpRequestFactory.setReadTimeout(readTimeoutMs);
        return clientHttpRequestFactory;
    }

    private record DelegationTokenRequestInterceptor(JwtProvider jwtProvider) implements ClientHttpRequestInterceptor {
        private static DelegationToken delegationToken = null;

        @Nullable
        private DelegationToken generateDelegationToken() {
            var token = this.jwtProvider.generateDelegationToken("pl.sknikod.kodemy", AUTHORITY);
            if (token == null) return null;
            return new DelegationToken(token.value(), token.expiration());
        }

        private boolean isTokenExpired() {
            return delegationToken.expiredDate.before(new Date());
        }

        @Override
        @NonNull
        public ClientHttpResponse intercept(
                @NonNull HttpRequest request,
                @NonNull byte[] body,
                @NonNull ClientHttpRequestExecution execution
        ) throws IOException {
            if (delegationToken == null || isTokenExpired())
                delegationToken = generateDelegationToken();
            if (delegationToken != null)
                request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + delegationToken.value);
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
