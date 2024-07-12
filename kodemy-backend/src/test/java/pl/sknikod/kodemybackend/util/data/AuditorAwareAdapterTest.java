package pl.sknikod.kodemybackend.util.data;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.AuditorAware;
import pl.sknikod.kodemybackend.BaseTest;
import pl.sknikod.kodemybackend.WithUserPrincipal;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuditorAwareAdapterTest extends BaseTest {
    AuditorAware<String> auditorAware = new AuditorAwareAdapter();

    private final static String USERNAME = "username";
    private final static String ANONYMOUS = "ANONYMOUS";

    @Test
    @WithUserPrincipal(username = USERNAME)
    void getCurrentAuditor_shouldReturnUsername() {
        // given
        // when
        Optional<String> result = auditorAware.getCurrentAuditor();
        // then
        assertTrue(result.isPresent());
        assertEquals(USERNAME.toUpperCase(), result.get());
    }

    @Test
    void getCurrentAuditor_shouldReturnAnonymous_whenNoAuthentication() {
        // given
        // when
        Optional<String> result = auditorAware.getCurrentAuditor();
        // then
        assertTrue(result.isPresent());
        assertEquals(ANONYMOUS, result.get());
    }

    @Test
    void getCurrentAuditor_shouldReturnAnonymous_whenAuthenticationNameNull() {
        // given
        // when
        Optional<String> result = auditorAware.getCurrentAuditor();
        // then
        assertTrue(result.isPresent());
        assertEquals(ANONYMOUS, result.get());
    }

}