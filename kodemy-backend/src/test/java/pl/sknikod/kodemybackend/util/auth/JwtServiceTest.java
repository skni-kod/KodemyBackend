package pl.sknikod.kodemybackend.util.auth;

import org.junit.jupiter.api.Test;
import pl.sknikod.kodemybackend.BaseTest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest extends BaseTest {
    static final JwtService.JwtProperties jwtProperties;

    static {
        jwtProperties = new JwtService.JwtProperties();
        jwtProperties.setSecretKey("YWJjZGVmZ2hjvbwjrW5vcHFyc3R1dnd4eXoxMjM0NTY3OnDMTIzNDU2Nzg5MDEyMzQ1Njc4OTAf=");
        JwtService.JwtProperties.Bearer bearer = new JwtService.JwtProperties.Bearer();
        bearer.setExpirationMin(0);
        jwtProperties.setBearer(bearer);
    }

    final JwtService jwtService = new JwtService(jwtProperties);

    private final JwtService.Input input = new JwtService.Input(
            1L, "username",
            false, false, false, true,
            Collections.emptySet()
    );

    @Test
    void generateUserToken_shouldSucceed() {
        //when
        JwtService.Token token = jwtService.generateUserToken(input);
        //then
        assertNotNull(token);
        assertNotNull(token.id());
        assertNotNull(token.value());
        assertNotNull(token.expiration());
    }

    @Test
    void parseToken_shouldSucceed() {
        //given
        var bearer_long_time = "eyJhbGciOiJIUzM4NCJ9.eyJpc3MiOiJwbC5za25pa29kLmtvZGVteSIsImp0aSI6ImM4MmY0NmU4LTM5NmEtNDUwYi1hMDEzLTM2OGI5ZDkzMDMyOCIsInN1YiI6InVzZXJuYW1lIiwiaWF0IjoxNzIwMzcyNzE1LCJleHAiOjE4NzgxNjA3MTUsImlkIjoxLCJzdGF0ZSI6OCwiYXV0aG9yaXRpZXMiOltdfQ.7yTYGNOlIC9aXdGoWdSTvMQe8mgUy15wlrekTugRIP8yp0BhfdxbmPCUL4nYpGFO";
        //when
        var result = jwtService.parseToken(bearer_long_time);
        //then
        assertTrue(result.isSuccess());
        var claims = result.get();
        assertEquals(input.getId(), claims.getId());
        assertEquals(input.getUsername(), claims.getUsername());
        assertEquals(input.getAuthorities().size(), claims.getAuthorities().size());
    }


    @Test
    void parseToken_shouldFailure_whenSignatureInvalid() {
        //given
        var bearer_bad_signature = "eyJhbGciOiJIUzM4NCJ9.eyJpc3MiOiJwbC5za25pa29kLmtvZGVteSIsImp0aSI6ImM4MmY0NmU4LTM5NmEtNDUwYi1hMDEzLTM2OGI5ZDkzMDMyOCIsInN1YiI6InVzZXJuYW1lIiwiaWF0IjoxNzIwMzcyNzE1LCJleHAiOjE4NzgxNjA3MTUsImlkIjoxLCJzdGF0ZSI6OCwiYXV0aG9yaXRpZXMiOltdfQ.fn5hBfjoKajVhoc1OmH9UR83TpNzhAwSjB9ttDJUzTFNMpswK2dmZDhUM-AZzS-m";
        //when
        var result = jwtService.parseToken(bearer_bad_signature);
        //then
        assertTrue(result.isFailure());
        assertEquals("JWT signature is invalid", result.getCause().getMessage());
    }

    @Test
    void parseToken_shouldFailure_whenExpired() {
        //given
        var bearer_expired = "eyJhbGciOiJIUzM4NCJ9.eyJpc3MiOiJwbC5za25pa29kLmtvZGVteSIsImp0aSI6ImE3YWJiMDQyLTE4MDktNDExYy05OTg2LWE5MjkyNjIxOGIxNSIsInN1YiI6InVzZXJuYW1lIiwiaWF0IjoxNzIwMzcyNzg2LCJleHAiOjE3MjAzNzI3ODYsImlkIjoxLCJzdGF0ZSI6OCwiYXV0aG9yaXRpZXMiOltdfQ.lSDWF3JtWQGAxcVAKowBXjhXcqdf2W9TUZzqhMDmX1jLh2Hmos98t0yN9rxYFtGJ";
        //when
        var result = jwtService.parseToken(bearer_expired);
        //then
        assertTrue(result.isFailure());
        assertEquals("JWT expired", result.getCause().getMessage());
    }

}