package pl.sknikod.kodemyauth.infrastructure.database;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderRepository extends JpaRepository<Provider, Long> {
}