package pl.sknikod.kodemyauth.infrastructure.common.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Token;

@Repository
public interface TokenRepository extends CrudRepository<Token, Long> {
}