package pl.sknikod.kodemygateway.infrastructure.module.oauth2.model;

import lombok.NonNull;
import lombok.Value;

import java.util.Map;

@Value
public class AuthorizeResponse {
    String accessToken;
    String refreshToken;

    public static AuthorizeResponse fromMap(@NonNull Map<String, Object> map) {
        Object accessToken = map.get("accessToken");
        Object refreshToken = map.get("refreshToken");
        if (accessToken == null || refreshToken == null) {
            throw new IllegalArgumentException("Map must contain non-null keys.");
        }
        return new AuthorizeResponse((String) accessToken, (String) refreshToken);
    }
}
