package pl.sknikod.kodemyauth.infrastructure.module.oauth2;

import io.vavr.control.Try;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

import java.io.Serializable;
import java.time.Duration;
import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthorizationRequestRepository implements
        AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    private static final String AUTH_REQ_PREFIX = "oauth2_auth_request";
    private static final Duration SESSION_STORE_DURATION = Duration.ofMinutes(5);
    private final StringRedisTemplate redisTemplate;

    private final HttpSessionOAuth2AuthorizationRequestRepository delegate =
            new HttpSessionOAuth2AuthorizationRequestRepository();

    @Override
    @Nullable
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return Try.of(() -> redisTemplate.opsForValue().get(getRedisKey(getStateParam(request))))
                .onFailure(th -> log.error("Problem with getting {} redis key value", AUTH_REQ_PREFIX, th))
                .mapTry(encodedReq -> (OAuth2AuthorizationRequest) Base64Coder.decode(encodedReq))
                .getOrNull();
    }

    private String getRedisKey(String state) {
        return AUTH_REQ_PREFIX + ":" + state;
    }

    private String getStateParam(HttpServletRequest request) {
        return request.getParameter(OAuth2ParameterNames.STATE);
    }

    @Override
    public void saveAuthorizationRequest(
            OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response
    ) {
        if (authorizationRequest == null) {
            removeAuthorizationRequest(request, response);
            return;
        }
        var state = authorizationRequest.getState();
        if (state != null) {
            Try.of(() -> {
                var encodedReq = Base64Coder.encode(authorizationRequest);
                redisTemplate.opsForValue().set(getRedisKey(state), encodedReq, SESSION_STORE_DURATION);
                return true;
            }).onFailure(th -> log.error("Problem with store {} redis key", AUTH_REQ_PREFIX, th));
        }
    }

    @Override
    @Nullable
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        OAuth2AuthorizationRequest authorizationRequest = loadAuthorizationRequest(request);
        if (authorizationRequest != null) {
            Try.of(() -> redisTemplate.delete(getRedisKey(getStateParam(request))))
                    .onFailure(th -> log.warn("Cannot delete redis {} key", AUTH_REQ_PREFIX, th));
        }
        return authorizationRequest;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static class Base64Coder {
        private static final Base64.Encoder ENCODER = Base64.getUrlEncoder();
        private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

        public static <T extends Serializable> String encode(T object) {
            return ENCODER.encodeToString(SerializationUtils.serialize(object));
        }

        public static Object decode(String src) {
            return SerializationUtils.deserialize(DECODER.decode(src));
        }
    }
}
