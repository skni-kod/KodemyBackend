package pl.sknikod.kodemygateway.util;

import lombok.NonNull;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

public class AuthCookies {
    private AuthCookies() {
    }

    public static final String ACCESS_TOKEN = "AUTH_CONTEXT";
    public static final String REFRESH_TOKEN = "AUTH_PERSIST";

    public static ResponseCookie create(@NonNull String name, @NonNull String value, @NonNull Duration age) {
        return ResponseCookie.from(name, value)
                .path("/")
                .httpOnly(true)
                .sameSite("Lax")
                .maxAge(age)
                .build();
    }
}
