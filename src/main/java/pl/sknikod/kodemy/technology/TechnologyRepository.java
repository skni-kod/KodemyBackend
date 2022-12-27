package pl.sknikod.kodemy.technology;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechnologyRepository extends PagingAndSortingRepository<Technology, Long> {
}