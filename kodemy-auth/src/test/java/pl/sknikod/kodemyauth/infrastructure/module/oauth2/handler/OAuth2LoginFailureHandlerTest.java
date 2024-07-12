package pl.sknikod.kodemyauth.infrastructure.module.oauth2.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import pl.sknikod.kodemyauth.BaseTest;
import pl.sknikod.kodemyauth.util.route.RouteRedirectStrategy;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OAuth2LoginFailureHandlerTest extends BaseTest {
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;

    @Mock
    RouteRedirectStrategy redirectStrategy;
    private static final String frontRouteBaseUrl = "http://localhost:8080";

    OAuth2LoginFailureHandler failureHandler;

    @SuppressWarnings("unchecked")
    @Captor
    ArgumentCaptor<Map<String, String>> paramsCaptor = ArgumentCaptor.forClass(Map.class);

    @BeforeEach
    void setUp() {
        failureHandler = new OAuth2LoginFailureHandler(frontRouteBaseUrl);
        failureHandler.setRedirectStrategy(redirectStrategy);
    }

    @Test
    void onAuthenticationFailure_shouldFailure_whenAuthenticationException() {
        // given
        AuthenticationException exception = mock(AuthenticationException.class);
        // when
        failureHandler.onAuthenticationFailure(request, response, exception);
        // then
        verify(redirectStrategy, times(1))
                .sendRedirect(eq(request), eq(response), eq(frontRouteBaseUrl), paramsCaptor.capture());

        var params = paramsCaptor.getValue();
        assertEquals("authentication_error", params.get("error"));
    }

    @Test
    void onAuthenticationFailure_shouldFailure_whenOAuth2AuthenticationException() {
        // given
        OAuth2AuthenticationException exception = mock(OAuth2AuthenticationException.class);
        // when
        failureHandler.onAuthenticationFailure(request, response, exception);
        // then
        verify(redirectStrategy, times(1))
                .sendRedirect(eq(request), eq(response), eq(frontRouteBaseUrl), paramsCaptor.capture());

        var params = paramsCaptor.getValue();
        assertEquals("authentication_error", params.get("error"));
    }

}