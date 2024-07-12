package pl.sknikod.kodemybackend.util.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import pl.sknikod.kodemybackend.BaseTest;
import pl.sknikod.kodemybackend.WithUserPrincipal;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AuthFacadeTest extends BaseTest {
    private static final String USERNAME = "username";

    @Test
    void getAuthentication_shouldEmpty_whenNoAuthentication() {
        // given
        // when
        Optional<Authentication> result = AuthFacade.getAuthentication();
        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @WithUserPrincipal
    void getAuthentication_shouldAuthentication_whenAuthenticationExists() {
        // given
        // when
        Optional<Authentication> result = AuthFacade.getAuthentication();
        // then
        assertTrue(result.isPresent());
        assertEquals(SecurityContextHolder.getContext().getAuthentication(), result.get());
    }

    @Test
    void isAuthenticated_shouldFalse_whenNoAuthentication() {
        // given
        // when
        boolean result = AuthFacade.isAuthenticated();
        // then
        assertFalse(result);
    }

    @Test
    @WithUserPrincipal(authenticated = false)
    void isAuthenticated_shouldFalse_whenAuthenticationNotAuthenticated() {
        // given
        // when
        boolean result = AuthFacade.isAuthenticated();
        // then
        assertFalse(result);
    }

    @Test
    @WithUserPrincipal
    void isAuthenticated_shouldTrue_whenAuthenticationIsAuthenticated() {
        // given
        // when
        boolean result = AuthFacade.isAuthenticated();
        // then
        assertTrue(result);
    }

    @Test
    void getCurrentUsername_shouldNull_whenNoAuthentication() {
        // given
        // when
        String result = AuthFacade.getCurrentUsername();
        // then
        assertNull(result);
    }

    @Test
    @WithUserPrincipal(username = USERNAME)
    void getCurrentUsername_shouldUsername_whenAuthenticationExists() {
        // given
        // when
        String result = AuthFacade.getCurrentUsername();
        // then
        assertEquals(USERNAME, result);
    }

    @Test
    void getCurrentUserPrincipal_shouldEmpty_whenNoAuthentication() {
        // given
        // when
        Optional<UserPrincipal> result = AuthFacade.getCurrentUserPrincipal();
        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @WithUserPrincipal
    void getCurrentUserPrincipal_shouldUserPrincipal_whenAuthenticationExists() {
        // given
        // when
        Optional<UserPrincipal> result = AuthFacade.getCurrentUserPrincipal();
        // then
        assertTrue(result.isPresent());
        assertEquals(SecurityContextHolder.getContext().getAuthentication().getPrincipal(), result.get());
    }

    @Test
    void getCurrentUserPrincipal_shouldEmpty_whenPrincipalIsNotUserPrincipal() {
        // given
        var userDetails = new UserDetailsImpl();
        var authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        // when
        Optional<UserPrincipal> result = AuthFacade.getCurrentUserPrincipal();
        // then
        assertTrue(result.isEmpty());
        SecurityContextHolder.clearContext();
    }

    @Test
    void setAuthentication_shouldSet_whenCalled() {
        // given
        var userDetails = new UserDetailsImpl();
        var authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        // when
        Authentication result = AuthFacade.setAuthentication(authenticationToken);
        // then
        assertEquals(SecurityContextHolder.getContext().getAuthentication(), result);
        SecurityContextHolder.clearContext();
    }

    @Test
    void setAuthentication_shouldSetAndClearContext_whenFlagIsTrue() {
        // given
        var userDetails = new UserDetailsImpl();
        var authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        // when
        Authentication result = AuthFacade.setAuthentication(authenticationToken, true);
        // then
        assertEquals(authenticationToken, result);
        SecurityContextHolder.clearContext();
    }

    private static class UserDetailsImpl implements UserDetails {
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return Collections.emptySet();
        }

        @Override
        public String getPassword() {
            return "";
        }

        @Override
        public String getUsername() {
            return USERNAME;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

}