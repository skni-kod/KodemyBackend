package pl.sknikod.kodemyauth.infrastructure.database.handler;

import io.vavr.control.Try;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pl.sknikod.kodemyauth.configuration.SecurityConfig;
import pl.sknikod.kodemyauth.exception.structure.NotFoundException;
import pl.sknikod.kodemyauth.exception.structure.ServerProcessingException;
import pl.sknikod.kodemyauth.factory.OAuth2Factory;
import pl.sknikod.kodemyauth.factory.RoleFactory;
import pl.sknikod.kodemyauth.factory.UserFactory;
import pl.sknikod.kodemyauth.infrastructure.database.entity.Role;
import pl.sknikod.kodemyauth.infrastructure.database.entity.User;
import pl.sknikod.kodemyauth.infrastructure.database.repository.RoleRepository;
import pl.sknikod.kodemyauth.infrastructure.database.repository.UserRepository;
import pl.sknikod.kodemyauth.util.BaseTest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class UserRepositoryHandlerTest extends BaseTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private SecurityConfig.RoleProperties roleProperties;

    private UserRepositoryHandler userRepositoryHandler;

    @BeforeEach
    void setUp() {
        userRepositoryHandler = new UserRepositoryHandler(userRepository, roleRepository, roleProperties);
    }

    @Test
    void save_shouldSucceed() {
        // given
        when(roleProperties.getPrimary())
                .thenReturn(Role.RoleName.ROLE_USER.name());
        when(roleRepository.findByName(any()))
                .thenReturn(Optional.of(RoleFactory.roleUser));
        when(userRepository.save(any()))
                .thenReturn(UserFactory.userUser);
        // when
        Optional<User> result = userRepositoryHandler.save(OAuth2Factory.oAuth2GithubUser());
        // then
        assertTrue(result.isPresent());
        assertEquals(UserFactory.userUser.getUsername(), result.get().getUsername());
    }

    @Test
    void save_shouldEmpty_whenRoleNotFound() {
        // given
        when(roleProperties.getPrimary())
                .thenReturn(Role.RoleName.ROLE_USER.name());
        when(roleRepository.findByName(any()))
                .thenReturn(Optional.empty());
        // when
        Optional<User> result = userRepositoryHandler.save(OAuth2Factory.oAuth2GithubUser());
        // then
        assertTrue(result.isEmpty());
    }

    @Test
    void save_shouldEmpty_whenSaveFails() {
        // given
        when(roleProperties.getPrimary())
                .thenReturn(Role.RoleName.ROLE_USER.name());
        when(roleRepository.findByName(any()))
                .thenReturn(Optional.of(RoleFactory.roleUser));
        when(userRepository.save(any()))
                .thenThrow(new OptimisticLockingFailureException(""));
        // when
        Optional<User> result = userRepositoryHandler.save(OAuth2Factory.oAuth2GithubUser());
        // then
        assertTrue(result.isEmpty());
    }

    @Test
    void findByProviderUser_shouldSucceed() {
        // given
        when(userRepository.findUserByPrincipalIdAndAuthProviderWithFetchRole(any(), any()))
                .thenReturn(UserFactory.userUser);
        // when
        Try<User> result = userRepositoryHandler.findByProviderUser(OAuth2Factory.oAuth2GithubUser());
        // then
        assertTrue(result.isSuccess());
        assertEquals(UserFactory.userUser.getUsername(), result.get().getUsername());
    }

    @Test
    void findUserByProvider_shouldFailure_whenUserNotFound() {
        // given
        when(userRepository.findUserByPrincipalIdAndAuthProviderWithFetchRole(any(), any()))
                .thenReturn(null);
        // when
        Try<User> result = userRepositoryHandler.findByProviderUser(OAuth2Factory.oAuth2GithubUser());
        // then
        assertTrue(result.isFailure());
        assertInstanceOf(NotFoundException.class, result.getCause());
    }

    @Test
    void findById_shouldSucceed() {
        // given
        when(userRepository.findById(UserFactory.userUser.getId()))
                .thenReturn(Optional.of(UserFactory.userUser));
        // when
        Try<User> result = userRepositoryHandler.findById(UserFactory.userUser.getId());
        // then
        assertTrue(result.isSuccess());
        assertEquals(UserFactory.userUser.getUsername(), result.get().getUsername());
    }

    @Test
    void findById_shouldFailure_whenUserNotFound() {
        // given
        when(userRepository.findById(UserFactory.userUser.getId()))
                .thenReturn(Optional.empty());
        // when
        Try<User> result = userRepositoryHandler.findById(UserFactory.userUser.getId());
        // then
        assertTrue(result.isFailure());
        assertInstanceOf(NotFoundException.class, result.getCause());
    }

    @Test
    void updateRole_shouldSucceed() {
        // given
        when(roleRepository.findByName(any()))
                .thenReturn(Optional.of(RoleFactory.roleAdmin));
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(UserFactory.userUser));
        when(userRepository.save(any()))
                .thenReturn(UserFactory.userAdmin);
        // when
        Try<User> result = userRepositoryHandler.updateRole(UserFactory.userUser.getId(), Role.RoleName.ROLE_ADMIN);
        // then
        assertTrue(result.isSuccess());
        assertEquals(RoleFactory.roleAdmin, result.get().getRole());
    }

    @Test
    void updateRole_shouldFailure_whenRoleNotFound() {
        // given
        when(roleRepository.findByName(any()))
                .thenReturn(Optional.empty());
        // when
        Try<User> result = userRepositoryHandler.updateRole(UserFactory.userUser.getId(), Role.RoleName.ROLE_ADMIN);
        // then
        assertTrue(result.isFailure());
        assertInstanceOf(ServerProcessingException.class, result.getCause());
    }

    @Test
    void updateRole_shouldFailure_whenUserNotFound() {
        // given
        when(roleRepository.findByName(any()))
                .thenReturn(Optional.of(RoleFactory.roleAdmin));
        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());
        // when
        Try<User> result = userRepositoryHandler.updateRole(UserFactory.userUser.getId(), Role.RoleName.ROLE_ADMIN);
        // then
        assertTrue(result.isFailure());
        assertInstanceOf(ServerProcessingException.class, result.getCause());
    }

    @Test
    void updateRole_shouldFailure_whenSaveFails() {
        // given
        when(roleRepository.findByName(any()))
                .thenReturn(Optional.of(RoleFactory.roleAdmin));
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(UserFactory.userUser));
        when(userRepository.save(any()))
                .thenThrow(new OptimisticLockingFailureException(""));
        // when
        Try<User> result = userRepositoryHandler.updateRole(UserFactory.userUser.getId(), Role.RoleName.ROLE_ADMIN);
        // then
        assertTrue(result.isFailure());
        assertInstanceOf(ServerProcessingException.class, result.getCause());
    }

    @Test
    void findByUsernameOrEmailOrRole_shouldSucceed() {
        // given
        Page<User> pageOfUser = new PageImpl<>(List.of(UserFactory.userUser), Pageable.unpaged(), 1);
        when(userRepository.findByUsernameOrEmailOrRole(any(), any(), any(), any()))
                .thenReturn(pageOfUser);
        // when
        Page<User> result = userRepositoryHandler.findByUsernameOrEmailOrRole(any(), any(), any(), any());
        // then
        assertNotNull(result);
        assertEquals(pageOfUser.getTotalPages(), result.getTotalPages());
        assertEquals(UserFactory.userUser.getUsername(), result.getContent().get(0).getUsername());
    }

    @Test
    void findByUsernameOrEmailOrRole_shouldSucceed_whenPageEmpty() {
        // given
        Page<User> pageOfUser = new PageImpl<>(Collections.emptyList(), Pageable.unpaged(), 0);
        when(userRepository.findByUsernameOrEmailOrRole(any(), any(), any(), any()))
                .thenReturn(pageOfUser);
        // when
        Page<User> result = userRepositoryHandler.findByUsernameOrEmailOrRole(any(), any(), any(), any());
        // then
        assertNotNull(result);
        assertEquals(pageOfUser.getTotalPages(), result.getTotalPages());
        assertEquals(pageOfUser.getContent().size(), result.getContent().size());
    }
}