package pl.sknikod.kodemyauth.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.servlet.http.Cookie;
import java.time.Duration;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieUtil {
    private static final String COOKIE_DOMAIN = "localhost";
    private static final Boolean HTTP_ONLY = Boolean.TRUE;
    private static final Boolean SECURE = Boolean.TRUE;
    private static final Boolean NON_SECURE = Boolean.FALSE;
    public static final Duration MAX_AGE = Duration.ofMillis(15_778_463);

    public static Cookie generate(
            @NonNull String name,
            @NonNull String value,
            @NonNull Duration expireTime,
            boolean httpOnly
    ) {
        var cookie = new javax.servlet.http.Cookie(name, value);
        if (!"localhost".equals(COOKIE_DOMAIN))
            cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath("/");
        cookie.setSecure(SECURE);
        cookie.setHttpOnly(httpOnly);
        cookie.setMaxAge((int) expireTime.toSeconds());
        return cookie;
    }

    public static Cookie generate(
            @NonNull String name,
            @NonNull String value,
            @NonNull Duration expireTime
    ) {
        return generate(name, value, expireTime, HTTP_ONLY);
    }

    public static Optional<String> get(Cookie[] cookies, @NonNull String name) {
        if (cookies != null)
            for (var cookie : cookies)
                if (cookie.getName().equalsIgnoreCase(name))
                    return Optional.ofNullable(cookie.getValue());
        return Optional.empty();
    }

    public static Optional<Cookie> expire(Cookie[] cookies, @NonNull String name) {
        if (cookies != null) {
            for (var cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }
}