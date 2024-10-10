package pl.sknikod.kodemyauth.infrastructure.database.handler;

import io.vavr.control.Try;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import pl.sknikod.kodemyauth.factory.RoleFactory;
import pl.sknikod.kodemyauth.infrastructure.database.Role;
import pl.sknikod.kodemyauth.infrastructure.database.RoleRepository;
import pl.sknikod.kodemyauth.BaseTest;
import pl.sknikod.kodemyauth.infrastructure.dao.RoleDao;
import pl.sknikod.kodemycommon.exception.NotFound404Exception;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RoleDaoTest extends BaseTest {
    @Mock
    private RoleRepository roleRepository;

    private RoleDao roleDao;

    @BeforeEach
    void setUp() {
        roleDao = new RoleDao(roleRepository);
    }

    @Test
    void findByRoleName_shouldSucceed() {
        // given
        when(roleRepository.findByName(any()))
                .thenReturn(Optional.of(RoleFactory.roleUser));
        // when
        Try<Role> result = roleDao.findByRoleName("ROLE_ADMIN");
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
        Try<Role> result = roleDao.findByRoleName("ROLE_ADMIN");
        // then
        assertTrue(result.isFailure());
        assertInstanceOf(NotFound404Exception.class, result.getCause());
    }
}