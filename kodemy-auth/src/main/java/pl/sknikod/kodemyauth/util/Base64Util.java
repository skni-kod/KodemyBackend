package pl.sknikod.kodemyauth.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Base64Util {
    public static <T> String encode(T object) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
    }

    public static Object decode(String src) {
        return SerializationUtils.deserialize(Base64.getUrlDecoder().decode(src));
    }
}
