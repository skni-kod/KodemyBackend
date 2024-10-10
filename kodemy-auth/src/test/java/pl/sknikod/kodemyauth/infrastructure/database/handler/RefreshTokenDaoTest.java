package pl.sknikod.kodemyauth.infrastructure.database.handler;

import io.vavr.control.Try;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.dao.OptimisticLockingFailureException;
import pl.sknikod.kodemyauth.factory.TokenFactory;
import pl.sknikod.kodemyauth.factory.UserFactory;
import pl.sknikod.kodemyauth.infrastructure.database.RefreshToken;
import pl.sknikod.kodemyauth.infrastructure.database.RefreshTokenRepository;
import pl.sknikod.kodemyauth.infrastructure.database.UserRepository;
import pl.sknikod.kodemyauth.BaseTest;
import pl.sknikod.kodemyauth.infrastructure.dao.RefreshTokenDao;
import pl.sknikod.kodemycommon.exception.InternalError500Exception;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class RefreshTokenDaoTest extends BaseTest {
    @Mock
    RefreshTokenRepository refreshTokenRepository;
    Integer refreshTokenExpirationMin = 1440;
    @Mock
    UserRepository userRepository;

    RefreshTokenDao refreshTokenRepositoryHandler;

    @BeforeEach
    void setUp() {
        refreshTokenRepositoryHandler = new RefreshTokenDao(
                refreshTokenRepository, refreshTokenExpirationMin, userRepository);
    }

    @Test
    void createAndGet_shouldSucceed() {
        //given
        var id = 1L;
        var uuid = UUID.randomUUID();
        when(userRepository.findById(id))
                .thenReturn(Optional.of(UserFactory.userUser));
        when(refreshTokenRepository.save(any()))
                .thenReturn(TokenFactory.refreshToken);
        //when
        Try<RefreshToken> result = refreshTokenRepositoryHandler.createAndGet(id, uuid);
        //then
        assertTrue(result.isSuccess());
        assertEquals(TokenFactory.refreshToken.getId(), result.get().getId());
        assertEquals(TokenFactory.refreshToken.getBearerId(), result.get().getBearerId());
    }

    @Test
    void createAndGet_shouldFailure_whenUserNotFound() {
        //given
        var id = 1L;
        var uuid = UUID.randomUUID();
        when(userRepository.findById(id))
                .thenReturn(null);
        //when
        Try<RefreshToken> result = refreshTokenRepositoryHandler.createAndGet(id, uuid);
        //then
        assertTrue(result.isFailure());
        assertInstanceOf(InternalError500Exception.class, result.getCause());
    }

    @Test
    void createAndGet_shouldFailure_whenSaveFails() {
        //given
        var id = 1L;
        var uuid = UUID.randomUUID();
        when(userRepository.findById(id))
                .thenReturn(Optional.of(UserFactory.userUser));
        when(refreshTokenRepository.save(any()))
                .thenThrow(new OptimisticLockingFailureException(""));
        //when
        Try<RefreshToken> result = refreshTokenRepositoryHandler.createAndGet(id, uuid);
        //then
        assertTrue(result.isFailure());
        assertInstanceOf(InternalError500Exception.class, result.getCause());
    }

    @Test
    void createAndGet2_shouldSucceed() {
        //given
        var uuid = UUID.randomUUID();
        when(refreshTokenRepository.save(any()))
                .thenReturn(TokenFactory.refreshToken);
        //when
        Try<RefreshToken> result = refreshTokenRepositoryHandler.createAndGet(UserFactory.userUser, uuid);
        //then
        assertTrue(result.isSuccess());
        assertEquals(TokenFactory.refreshToken.getId(), result.get().getId());
        assertEquals(TokenFactory.refreshToken.getBearerId(), result.get().getBearerId());
    }

    @Test
    void createAndGet2_shouldFailure_whenUserHasIdNull() {
        //given
        var uuid = UUID.randomUUID();
        var user = UserFactory.userUser;
        user.setId(null);
        when(refreshTokenRepository.save(any()))
                .thenThrow(new RuntimeException(""));
        //when
        Try<RefreshToken> result = refreshTokenRepositoryHandler.createAndGet(UserFactory.userUser, uuid);
        //then
        assertTrue(result.isFailure());
        assertInstanceOf(InternalError500Exception.class, result.getCause());
    }

    @Test
    void createAndGet2_shouldFailure_whenSaveFails() {
        //given
        var uuid = UUID.randomUUID();
        when(refreshTokenRepository.save(any()))
                .thenThrow(new OptimisticLockingFailureException(""));
        //when
        Try<RefreshToken> result = refreshTokenRepositoryHandler.createAndGet(UserFactory.userUser, uuid);
        //then
        assertTrue(result.isFailure());
        assertInstanceOf(InternalError500Exception.class, result.getCause());
    }
}