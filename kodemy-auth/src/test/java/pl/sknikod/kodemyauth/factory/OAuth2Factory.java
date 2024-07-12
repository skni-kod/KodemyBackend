package pl.sknikod.kodemyauth.factory;

import pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.OAuth2Provider;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.github.GithubOAuth2Provider;

import java.util.Map;

public class OAuth2Factory {
    private OAuth2Factory(){

    }

    public static OAuth2Provider.User oAuth2GithubUser(){
        return new GithubOAuth2Provider.GithubUser(Map.of(
                "id", 1L,
                "login", "username",
                "email", "email@email.com",
                "avatar_url", "photo"
        ));
    }
}
