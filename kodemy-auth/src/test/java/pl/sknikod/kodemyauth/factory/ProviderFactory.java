package pl.sknikod.kodemyauth.factory;

import pl.sknikod.kodemyauth.infrastructure.database.entity.Provider;
import pl.sknikod.kodemyauth.infrastructure.database.entity.Role;

public class ProviderFactory {
    private ProviderFactory(){
    }

    public static Provider provider(String providerType){
        var provider = new Provider();
        provider.setId(1L);
        provider.setProviderType(providerType);
        provider.setUser(UserFactory.user(RoleFactory.role(Role.RoleName.ROLE_USER)));
        return provider;
    }
}
