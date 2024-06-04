package pl.sknikod.kodemyauth.infrastructure.module;

import io.vavr.control.Try;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.sknikod.kodemyauth.configuration.SecurityConfig;
import pl.sknikod.kodemyauth.factory.TokenFactory;
import pl.sknikod.kodemyauth.infrastructure.database.handler.RefreshTokenRepositoryHandler;
import pl.sknikod.kodemyauth.util.BaseTest;
import pl.sknikod.kodemyauth.util.auth.JwtService;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefreshTokensUseCaseTest extends BaseTest {
    private final RefreshTokenRepositoryHandler refreshTokenRepositoryHandler =
            Mockito.mock(RefreshTokenRepositoryHandler.class);
    private final JwtService jwtService =
            Mockito.mock(JwtService.class);
    private SecurityConfig.RoleProperties roleProperties =
            Mockito.mock(SecurityConfig.RoleProperties.class);

    private final RefreshTokensUseCase refreshTokensUseCase =
            new RefreshTokensUseCase(refreshTokenRepositoryHandler, jwtService, roleProperties);

    private static final UUID refresh;
    private static final UUID bearerJti;

    static {
        refresh = UUID.randomUUID();
        bearerJti = UUID.randomUUID();
    }

    @BeforeEach
    void setUp() {
        when(roleProperties.getAuthorities(any()))
                .thenReturn(Collections.emptySet());
    }

    @Test
    void refresh_shouldReturnNewTokens() {
        // given
        var refreshToken = TokenFactory.refreshToken();
        when(refreshTokenRepositoryHandler.findByTokenAndBearerJti(eq(refresh), eq(bearerJti)))
                .thenReturn(Try.success(refreshToken));
        when(jwtService.generateUserToken(any()))
                .thenReturn(TokenFactory.jwtServiceToken);
        when(refreshTokenRepositoryHandler.createAndGet(any(), any()))
                .thenReturn(Try.success(refreshToken));
        // when
        var result = refreshTokensUseCase.refresh(refresh, bearerJti);
        // then
        assertEquals(TokenFactory.jwtServiceToken.value(), result.token());
        assertEquals(refreshToken.getToken().toString(), result.refresh());
    }
}