package pl.sknikod.kodemyauth.infrastructure.module.oauth2.handler;

import io.vavr.control.Try;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import pl.sknikod.kodemyauth.exception.ExceptionPattern;
import pl.sknikod.kodemyauth.exception.structure.NotFoundException;
import pl.sknikod.kodemyauth.factory.TokenFactory;
import pl.sknikod.kodemyauth.factory.UserFactory;
import pl.sknikod.kodemyauth.infrastructure.database.entity.User;
import pl.sknikod.kodemyauth.infrastructure.database.handler.RefreshTokenRepositoryHandler;
import pl.sknikod.kodemyauth.BaseTest;
import pl.sknikod.kodemyauth.util.auth.JwtService;
import pl.sknikod.kodemyauth.util.route.RouteRedirectStrategy;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OAuth2LoginSuccessHandlerTest extends BaseTest {
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    Authentication authentication;

    @Autowired
    JwtService jwtService;
    @Mock
    RefreshTokenRepositoryHandler refreshTokenRepositoryHandler;
    @Mock
    RouteRedirectStrategy redirectStrategy;
    static final String frontRouteBaseUrl = "http://localhost:8080";

    OAuth2LoginSuccessHandler successHandler;

    @SuppressWarnings("unchecked")
    @Captor
    ArgumentCaptor<Map<String, String>> paramsCaptor = ArgumentCaptor.forClass(Map.class);

    @BeforeEach
    void setUp() {
        successHandler = new OAuth2LoginSuccessHandler(jwtService, frontRouteBaseUrl, refreshTokenRepositoryHandler);
        successHandler.setRedirectStrategy(redirectStrategy);
    }

    @Test
    void onAuthenticationSuccess_shouldSucceed() {
        //given
        when(authentication.getPrincipal())
                .thenReturn(UserFactory.userPrincipal);
        when(jwtService.generateUserToken(any())).thenReturn(TokenFactory.jwtServiceToken);
        when(refreshTokenRepositoryHandler
                .createAndGet(UserFactory.userPrincipal.getId(), TokenFactory.jwtServiceToken.id()))
                .thenReturn(Try.success(TokenFactory.refreshToken));

        //when
        successHandler.onAuthenticationSuccess(request, response, authentication);

        //then
        verify(redirectStrategy, times(1))
                .sendRedirect(eq(request), eq(response), eq(frontRouteBaseUrl), paramsCaptor.capture());

        var params = paramsCaptor.getValue();
        assertEquals("header.payload.signature", params.get("token"));
        assertEquals(TokenFactory.refreshToken.getToken().toString(), params.get("refresh"));
    }

    @Test
    void onAuthenticationSuccess_shouldSucceed_whenPrincipalNull() {
        //given
        when(authentication.getPrincipal()).thenReturn(null);
        //when
        successHandler.onAuthenticationSuccess(request, response, authentication);
        //then
        verify(redirectStrategy, times(1))
                .sendRedirect(eq(request), eq(response), eq(frontRouteBaseUrl), paramsCaptor.capture());
        assertEquals("internal_error", paramsCaptor.getValue().get("error"));
    }

    @Test
    void onAuthenticationSuccess_shouldSucceed_whenGenerateTokenFails() {
        //given
        when(authentication.getPrincipal())
                .thenReturn(UserFactory.userPrincipal);
        when(jwtService.generateUserToken(any())).thenReturn(null);
        when(refreshTokenRepositoryHandler.createAndGet(UserFactory.userPrincipal.getId(), TokenFactory.jwtServiceToken.id()))
                .thenReturn(Try.failure(new NotFoundException(ExceptionPattern.ENTITY_NOT_FOUND_BY_PARAM, User.class, "id", 1L)));
        //when
        successHandler.onAuthenticationSuccess(request, response, authentication);
        //then
        verify(redirectStrategy, times(1))
                .sendRedirect(eq(request), eq(response), eq(frontRouteBaseUrl), paramsCaptor.capture());
        assertEquals("internal_error", paramsCaptor.getValue().get("error"));
    }

    @Test
    void onAuthenticationSuccess_shouldSucceed_whenCreateAndGetFails() {
        //given
        when(authentication.getPrincipal())
                .thenReturn(UserFactory.userPrincipal);
        when(jwtService.generateUserToken(any())).thenReturn(TokenFactory.jwtServiceToken);
        //when
        successHandler.onAuthenticationSuccess(request, response, authentication);
        //then
        verify(redirectStrategy, times(1))
                .sendRedirect(eq(request), eq(response), eq(frontRouteBaseUrl), paramsCaptor.capture());
        assertEquals("internal_error", paramsCaptor.getValue().get("error"));
    }
}