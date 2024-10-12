package pl.sknikod.kodemyauth.infrastructure.module;

import io.vavr.control.Try;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.sknikod.kodemyauth.configuration.SecurityConfiguration;
import pl.sknikod.kodemyauth.factory.TokenFactory;
import pl.sknikod.kodemyauth.infrastructure.dao.RefreshTokenDao;
import pl.sknikod.kodemyauth.BaseTest;
import pl.sknikod.kodemyauth.infrastructure.database.RoleRepository;
import pl.sknikod.kodemyauth.infrastructure.module.auth.RefreshTokensService;
import pl.sknikod.kodemycommons.security.JwtProvider;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefreshTokensServiceTest extends BaseTest {
    private final RefreshTokenDao refreshTokenRepositoryHandler =
            Mockito.mock(RefreshTokenDao.class);
    private final JwtProvider jwtProvider =
            Mockito.mock(JwtProvider.class);
    private final SecurityConfiguration.RoleProperties roleProperties =
            Mockito.mock(SecurityConfiguration.RoleProperties.class);
    private final RoleRepository roleRepository =
            Mockito.mock(RoleRepository.class);

    private final RefreshTokensService refreshTokensService =
            new RefreshTokensService(roleRepository, refreshTokenRepositoryHandler, jwtProvider, roleProperties);

    private static final UUID refresh;
    private static final UUID bearerJti;

    static {
        refresh = UUID.randomUUID();
        bearerJti = UUID.randomUUID();
    }

    @BeforeEach
    void setUp() {
        /*when(roleProperties.getAuthorities(any()))
                .thenReturn(Collections.emptySet());*/
    }

    @Test
    void refresh_shouldReturnNewTokens() {
        // given
        var refreshToken = TokenFactory.refreshToken();
        when(refreshTokenRepositoryHandler.findByTokenAndBearerJti(eq(refresh), eq(bearerJti)))
                .thenReturn(Try.success(refreshToken));
        when(jwtProvider.generateUserToken(any()))
                .thenReturn(TokenFactory.jwtProviderToken);
        when(refreshTokenRepositoryHandler.createAndGet(any(), any()))
                .thenReturn(Try.success(refreshToken));
        // when
        var result = refreshTokensService.refresh(refresh, bearerJti);
        // then
        assertEquals(TokenFactory.jwtProviderToken.value(), result.token());
        assertEquals(refreshToken.getToken().toString(), result.refresh());
    }
}