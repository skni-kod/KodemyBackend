package pl.sknikod.kodemyauth.util;

import lombok.NonNull;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class EncryptionUtil {
    private static final int KEY_ITERATION_COUNT = 100_000;
    private static final int KEY_SIZE_BYTES = 32;
    private static final int IV_SIZE_BYTES = 16;

    @SneakyThrows
    public static SecretKey generateKey(
            @NonNull String password, byte @NonNull [] salt
    ) {
        var factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        var keySpec = new PBEKeySpec(
                password.toCharArray(), salt,
                KEY_ITERATION_COUNT, KEY_SIZE_BYTES * 8
        );
        var secretKey = factory.generateSecret(keySpec);
        keySpec.clearPassword();
        return new SecretKeySpec(secretKey.getEncoded(), "AES");
    }

    @SneakyThrows
    public static byte[] encrypt(@NonNull SecretKey secretKey, byte @NonNull [] value) {
        var cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[IV_SIZE_BYTES];
        secureRandom.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

        byte[] encrypted = cipher.doFinal(value);
        byte[] encryptedWithIv = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
        System.arraycopy(encrypted, 0, encryptedWithIv, iv.length, encrypted.length);

        return encryptedWithIv;
    }

    @SneakyThrows
    public static byte[] decrypt(@NonNull SecretKey secretKey, byte @NonNull [] encryptValue) {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        byte[] iv = new byte[IV_SIZE_BYTES];
        System.arraycopy(encryptValue, 0, iv, 0, iv.length);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        byte[] encryptedValueWithoutIv = new byte[encryptValue.length - IV_SIZE_BYTES];
        System.arraycopy(encryptValue, IV_SIZE_BYTES, encryptedValueWithoutIv, 0, encryptedValueWithoutIv.length);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        return cipher.doFinal(encryptedValueWithoutIv);
    }
}
