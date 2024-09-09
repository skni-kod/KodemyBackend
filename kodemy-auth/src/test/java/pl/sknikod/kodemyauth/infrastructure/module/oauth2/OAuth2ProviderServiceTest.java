package pl.sknikod.kodemyauth.infrastructure.module.oauth2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.OAuth2Provider;
import pl.sknikod.kodemyauth.BaseTest;

import java.util.List;

import static org.mockito.Mockito.*;

class OAuth2ProviderServiceTest extends BaseTest {
    @Mock
    private OAuth2Provider oAuth2Provider1;

    private OAuth2ProviderService oAuth2ProviderService;

    @BeforeEach
    void setUp() {
        oAuth2ProviderService = new OAuth2ProviderService(List.of(oAuth2Provider1));

        when(oAuth2Provider1.getRegistrationId())
                .thenReturn("github");
    }

    @Test
    void getProviders_shouldReturnProviderRegistrationIds() {
        // when
        //String[] providers = oAuth2ProviderUseCase.getProviders();
        // then
        //assertArrayEquals(new String[]{"github"}, providers);
    }
}