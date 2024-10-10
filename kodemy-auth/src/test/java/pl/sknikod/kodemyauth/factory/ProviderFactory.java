package pl.sknikod.kodemyauth.factory;

import pl.sknikod.kodemyauth.infrastructure.database.model.Provider;
import pl.sknikod.kodemyauth.infrastructure.database.model.Role;

public class ProviderFactory {
    private ProviderFactory(){
    }

    public static Provider provider(String providerType){
        var provider = new Provider();
        provider.setId(1L);
        provider.setProviderType(providerType);
        provider.setUser(UserFactory.user(RoleFactory.role("ROLE_USER")));
        return provider;
    }
}
