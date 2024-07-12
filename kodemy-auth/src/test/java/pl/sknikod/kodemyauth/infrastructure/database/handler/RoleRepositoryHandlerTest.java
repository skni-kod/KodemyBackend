package pl.sknikod.kodemyauth.infrastructure.database.handler;

import io.vavr.control.Try;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import pl.sknikod.kodemyauth.exception.structure.NotFoundException;
import pl.sknikod.kodemyauth.factory.RoleFactory;
import pl.sknikod.kodemyauth.infrastructure.database.entity.Role;
import pl.sknikod.kodemyauth.infrastructure.database.repository.RoleRepository;
import pl.sknikod.kodemyauth.BaseTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RoleRepositoryHandlerTest extends BaseTest {
    @Mock
    private RoleRepository roleRepository;

    private RoleRepositoryHandler roleRepositoryHandler;

    @BeforeEach
    void setUp() {
        roleRepositoryHandler = new RoleRepositoryHandler(roleRepository);
    }

    @Test
    void findByRoleName_shouldSucceed() {
        // given
        when(roleRepository.findByName(any()))
                .thenReturn(Optional.of(RoleFactory.roleUser));
        // when
        Try<Role> result = roleRepositoryHandler.findByRoleName(Role.RoleName.ROLE_ADMIN.name());
        // then
        assertTrue(result.isSuccess());
        assertEquals(RoleFactory.roleUser.getName(), result.get().getName());
    }

    @Test
    void findByRoleName_shouldFailure_whenRoleNotFound() {
        // given
        when(roleRepository.findByName(any()))
                .thenReturn(Optional.empty());
        // when
        Try<Role> result = roleRepositoryHandler.findByRoleName(Role.RoleName.ROLE_ADMIN.name());
        // then
        assertTrue(result.isFailure());
        assertInstanceOf(NotFoundException.class, result.getCause());
    }
}