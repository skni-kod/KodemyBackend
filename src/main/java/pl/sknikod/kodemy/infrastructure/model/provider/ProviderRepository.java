package pl.sknikod.kodemy.infrastructure.model.provider;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderRepository extends JpaRepository<Provider, Long> {
}