package pl.sknikod.kodemyauth.infrastructure.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Provider;

public interface ProviderRepository extends JpaRepository<Provider, Long> {
}