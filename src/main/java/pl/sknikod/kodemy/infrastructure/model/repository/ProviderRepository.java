package pl.sknikod.kodemy.infrastructure.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sknikod.kodemy.infrastructure.model.entity.Provider;

public interface ProviderRepository extends JpaRepository<Provider, Long> {
}