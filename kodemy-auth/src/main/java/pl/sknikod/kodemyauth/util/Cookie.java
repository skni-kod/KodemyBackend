package pl.sknikod.kodemyauth.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Cookie {

    public static void addCookie(HttpServletResponse response, String name, String value){
        addCookie(response, name, value, 15778463);
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int expireTime) {
        var cookie = new javax.servlet.http.Cookie(name, value);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(expireTime);
        response.addCookie(cookie);
    }

    public static String getCookie(HttpServletRequest request, String name) {
        var cookies = request.getCookies();
        if (cookies != null) {
            for (var cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static javax.servlet.http.Cookie[] getCookies(HttpServletRequest request) {
        return request.getCookies();
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        var cookies = request.getCookies();
        if (cookies != null) {
            for (var cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    public static void clearAll(HttpServletRequest req, HttpServletResponse res) {
        for (var cookie : req.getCookies()) {
            String cookieName = cookie.getName();
            var cookieToDelete = new javax.servlet.http.Cookie(cookieName, null);
            cookieToDelete.setSecure(true);
            cookie.setHttpOnly(true);
            cookieToDelete.setMaxAge(0);
            res.addCookie(cookieToDelete);
        }
    }
}