package pl.sknikod.kodemyauth.util.data;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import pl.sknikod.kodemyauth.BaseTest;
import pl.sknikod.kodemyauth.WithUserPrincipal;
import pl.sknikod.kodemyauth.infrastructure.module.auth.RefreshTokensService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuditorAwareAdapterTest extends BaseTest {
    @Autowired
    RefreshTokensService refreshTokensService;

    AuditorAware<Long> auditorAware = new AuditorAwareAdapter();

    private final static Long USER = 1L;
    private final static Long ANONYMOUS = null;

    @Test
    @WithUserPrincipal
    void getCurrentAuditor_shouldReturnUsername() {
        // given
        // when
        var result = auditorAware.getCurrentAuditor();
        // then
        assertTrue(result.isPresent());
        assertEquals(USER, result.get());
    }

    @Test
    void getCurrentAuditor_shouldReturnAnonymous_whenNoAuthentication() {
        // given
        // when
        var result = auditorAware.getCurrentAuditor();
        // then
        assertTrue(result.isPresent());
        assertEquals(ANONYMOUS, result.get());
    }

    @Test
    void getCurrentAuditor_shouldReturnAnonymous_whenAuthenticationNameNull() {
        // given
        // when
        var result = auditorAware.getCurrentAuditor();
        // then
        assertTrue(result.isPresent());
        assertEquals(ANONYMOUS, result.get());
    }

}