package pl.sknikod.kodemyauth.infrastructure.module.oauth2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.OAuth2Provider;
import pl.sknikod.kodemyauth.util.BaseTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OAuth2ProviderUseCaseTest extends BaseTest {
    @Mock
    private OAuth2Provider oAuth2Provider1;

    private OAuth2ProviderUseCase oAuth2ProviderUseCase;

    @BeforeEach
    void setUp() {
        oAuth2ProviderUseCase = new OAuth2ProviderUseCase(List.of(oAuth2Provider1));

        when(oAuth2Provider1.getRegistrationId())
                .thenReturn("github");
    }

    @Test
    void getProviders_shouldReturnProviderRegistrationIds() {
        // when
        String[] providers = oAuth2ProviderUseCase.getProviders();
        // then
        assertArrayEquals(new String[]{"github"}, providers);
    }
}