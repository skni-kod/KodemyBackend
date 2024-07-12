package pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider;

import io.vavr.control.Try;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.util.Assert;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.util.OAuth2RestOperations;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public class OAuth2Workflow {
    protected final OAuth2RestOperations restOperations;
    protected static final ParameterizedTypeReference<Map<String, Object>> ATTRIBUTES_TYPE_REF =
            new ParameterizedTypeReference<>() {
            };
    protected static final Consumer<HttpHeaders> PASSIVE_HEADERS_CONSUMER = headers -> {
    };
    private State state = State.LOADING;
    protected final OAuth2UserRequest userRequest;
    protected final Map<String, Object> attributes;

    public OAuth2Workflow(
            @NonNull OAuth2RestOperations oAuth2RestOperations, @NonNull OAuth2UserRequest userRequest
    ) {
        Assert.notNull(oAuth2RestOperations, "oAuth2RestOperations cannot be null");
        Assert.notNull(userRequest, "userRequest cannot be null");
        this.restOperations = oAuth2RestOperations;
        this.userRequest = userRequest;
        this.attributes = new HashMap<>();
    }

    public final Map<String, Object> retrieve() {
        if (!isFinished()) {
            processBaseAttributes();
            addPostProcesses();
            state = State.FINISHED;
        }
        return attributes;
    }

    public boolean isFinished() {
        return state == State.FINISHED;
    }

    protected void addPostProcesses() {
        // placeholder to children
    }

    protected final void processBaseAttributes() {
        Try.of(() -> this.restOperations.exchange(this.userRequest).getBody())
                .onSuccess(attrs -> log.info("Successfully retrieved {} user attributes", attrs.size()))
                .peek(this.attributes::putAll);
    }

    private enum State {
        LOADING,
        FINISHED
    }
}