package pl.sknikod.kodemy.type;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeRepository extends PagingAndSortingRepository<Type,Integer> {
}
