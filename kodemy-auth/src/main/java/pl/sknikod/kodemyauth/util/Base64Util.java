package pl.sknikod.kodemyauth.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.SerializationUtils;

import java.util.Base64;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Base64Util {
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    public static <T> String encode(T object, Function<byte[], byte[]> afterSerialization) {
        return ENCODER.encodeToString(
                afterSerialization.apply(SerializationUtils.serialize(object)));
    }

    public static <T> String encode(T object) {
        return encode(object, Function.identity());
    }

    public static Object decode(String src, Function<byte[], byte[]> afterDecode) {
        return SerializationUtils.deserialize(afterDecode.apply(DECODER.decode(src)));
    }

    public static Object decode(String src) {
        return decode(src, Function.identity());
    }
}
