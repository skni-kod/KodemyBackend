package pl.sknikod.kodemy.infrastructure.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sknikod.kodemy.infrastructure.common.entity.Provider;

public interface ProviderRepository extends JpaRepository<Provider, Long> {
}