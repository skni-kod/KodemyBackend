package pl.sknikod.kodemyauth.infrastructure.module.oauth2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import pl.sknikod.kodemyauth.infrastructure.dao.UserDao;
import pl.sknikod.kodemyauth.infrastructure.database.RoleRepository;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.OAuth2Provider;

import java.util.List;

@Configuration
public class OAuth2BeanConfiguration {
    @Bean
    public OAuth2Service oAuth2Service(
            RoleRepository roleRepository,
            RestTemplate oAuth2RestTemplate,
            List<OAuth2Provider> oAuth2Providers,
            UserDao userDao
    ) {
        return new OAuth2Service(roleRepository, oAuth2RestTemplate, oAuth2Providers, userDao);
    }

    @Bean
    public OAuth2ProviderService oauth2ProviderService(
            List<OAuth2Provider> oAuth2Providers,
            @Value("${app.security.oauth2.endpoints.authorize}") String authorizeEndpoint
    ) {
        return new OAuth2ProviderService(oAuth2Providers, authorizeEndpoint);
    }
}
