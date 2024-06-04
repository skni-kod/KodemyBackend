package pl.sknikod.kodemyauth.infrastructure.database.handler;

import io.vavr.control.Try;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.dao.OptimisticLockingFailureException;
import pl.sknikod.kodemyauth.exception.structure.ServerProcessingException;
import pl.sknikod.kodemyauth.factory.TokenFactory;
import pl.sknikod.kodemyauth.factory.UserFactory;
import pl.sknikod.kodemyauth.infrastructure.database.entity.RefreshToken;
import pl.sknikod.kodemyauth.infrastructure.database.repository.RefreshTokenRepository;
import pl.sknikod.kodemyauth.infrastructure.database.repository.UserRepository;
import pl.sknikod.kodemyauth.util.BaseTest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class RefreshTokenRepositoryHandlerTest extends BaseTest {
    @Mock
    RefreshTokenRepository refreshTokenRepository;
    Integer refreshTokenExpirationMin = 1440;
    @Mock
    UserRepository userRepository;

    RefreshTokenRepositoryHandler refreshTokenRepositoryHandler;

    @BeforeEach
    void setUp() {
        refreshTokenRepositoryHandler = new RefreshTokenRepositoryHandler(
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
        assertInstanceOf(ServerProcessingException.class, result.getCause());
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
        assertInstanceOf(ServerProcessingException.class, result.getCause());
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
        assertInstanceOf(ServerProcessingException.class, result.getCause());
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
        assertInstanceOf(ServerProcessingException.class, result.getCause());
    }
}