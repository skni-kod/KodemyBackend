package pl.sknikod.kodemy.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Cookie {

    public static void addCookie(HttpServletResponse response, String name, String value, int expireTime) {
        var cookie = new javax.servlet.http.Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(expireTime);
        response.addCookie(cookie);
    }

    public static String getCookie(HttpServletRequest request, String name) {
        var cookies = request.getCookies();
        if (cookies != null && cookies.length > 0){
            for (var cookie : cookies){
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        var cookies = request.getCookies();
        if (cookies != null && cookies.length > 0){
            for (var cookie : cookies){
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }
}