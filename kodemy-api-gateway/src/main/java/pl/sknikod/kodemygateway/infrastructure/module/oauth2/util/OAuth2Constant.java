package pl.sknikod.kodemygateway.infrastructure.module.oauth2.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

import static org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver.DEFAULT_REGISTRATION_ID_URI_VARIABLE_NAME;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OAuth2Constant {
    public static final String OAUTH2_PROVIDER_SUFFIX = "/{" + DEFAULT_REGISTRATION_ID_URI_VARIABLE_NAME + "}";

    public static final String STATE_DIFFERENT = "different_state_param";
    public static final String REDIRECT_URI_REQUIRED = OAuth2ParameterNames.REDIRECT_URI + "_param_required";

    public static final String REDIRECT_URI_PARAM = OAuth2ParameterNames.REDIRECT_URI;
    public static final String REDIRECT_URI_COOKIE = "REDIRECTURI";
}
