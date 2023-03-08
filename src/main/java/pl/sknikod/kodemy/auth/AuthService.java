package pl.sknikod.kodemy.auth;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    public Map<String, String> getUserInfo(OAuth2AuthenticationToken authToken) {
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("name", authToken.getName());
        userInfo.put("authorities", authToken.getAuthorities().toString());
        return userInfo;
    }
}