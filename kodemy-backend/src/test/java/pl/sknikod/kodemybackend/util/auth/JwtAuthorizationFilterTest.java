package pl.sknikod.kodemybackend.util.auth;

import io.vavr.control.Try;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.sknikod.kodemybackend.factory.TokenFactory;
import pl.sknikod.kodemybackend.util.BaseTest;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class JwtAuthorizationFilterTest extends BaseTest {
    final static List<String> PERMIT_PATHS = List.of("api/something");

    @Autowired
    JwtService jwtService;
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    FilterChain filterChain;
    JwtAuthorizationFilter jwtAuthorizationFilter;

    @BeforeEach
    void setUp() {
        jwtAuthorizationFilter = new JwtAuthorizationFilter(PERMIT_PATHS, jwtService);
    }

    @Test
    void shouldNotFilter_shouldTrue() {
        //given
        when(request.getServletPath()).thenReturn(PERMIT_PATHS.get(0));
        //when
        boolean result = jwtAuthorizationFilter.shouldNotFilter(request);
        //then
        assertTrue(result);
    }

    @Test
    void shouldNotFilter_shouldFalse() {
        //given
        when(request.getServletPath()).thenReturn("/api/other");
        //when
        boolean result = jwtAuthorizationFilter.shouldNotFilter(request);
        //then
        assertFalse(result);
    }

    @Test
    void doFilterInternal_shouldAuthSucceed() throws ServletException, IOException {
        //given
        when(request.getHeader(any()))
                .thenReturn("Bearer header.payload.signature");
        when(jwtService.parseToken(any()))
                .thenReturn(Try.success(TokenFactory.jwtServiceTokenDeserialize));
        //when
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);
        //then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertInstanceOf(UsernamePasswordAuthenticationToken.class, authentication);
        assertInstanceOf(UserPrincipal.class, authentication.getPrincipal());
        assertEquals(TokenFactory.jwtServiceTokenDeserialize.getUsername(), ((UserPrincipal) authentication.getPrincipal()).getUsername());
    }

    @Test
    void doFilterInternal_shouldAuthFailure_whenHeaderEmpty() throws ServletException, IOException {
        //given
        when(request.getHeader(any()))
                .thenReturn(null);
        when(jwtService.parseToken(any()))
                .thenReturn(Try.success(TokenFactory.jwtServiceTokenDeserialize));
        //when
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);
        //then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_shouldAuthFailure_whenParseTokenEmpty() throws ServletException, IOException {
        //given
        when(request.getHeader(any()))
                .thenReturn("Bearer header.payload.signature");
        when(jwtService.parseToken(any()))
                .thenReturn(Try.failure(new RuntimeException()));
        //when
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);
        //then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}