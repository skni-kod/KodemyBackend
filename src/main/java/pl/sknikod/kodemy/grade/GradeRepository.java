package pl.sknikod.kodemy.grade;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRepository extends PagingAndSortingRepository<Grade, Long> {
}
