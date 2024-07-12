package pl.sknikod.kodemyauth.infrastructure.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.sknikod.kodemyauth.infrastructure.database.entity.Provider;

public interface ProviderRepository extends JpaRepository<Provider, Long> {
}