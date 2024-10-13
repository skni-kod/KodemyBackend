package pl.sknikod.kodemybackend.infrastructure.database;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
	Date DATE_MIN = Date.from(LocalDateTime.of(2023, 1, 1, 0, 0, 0)
			.atZone(ZoneId.systemDefault()).toInstant());
	Date DATE_MAX = Date.from(LocalDateTime.of(9999, 12, 31, 23, 59, 59)
			.atZone(ZoneId.systemDefault()).toInstant()
	);

	List<Grade> findAllByMaterialId(Long id);

	Long countAllByMaterialIdAndValue(Long id, Double value);

	@Query("SELECT COALESCE(AVG(g.value), 0.00) FROM Grade g WHERE g.material.id = :materialId")
	Double findAvgGradeByMaterialId(Long materialId);

	@Query(
			value = "SELECT COALESCE(AVG(g.value), 0.00) FROM Grade g " +
					"WHERE g.material.id IN :materialIds GROUP BY g.material.id")
	List<Double> findAvgGradesByMaterialIdIn(List<Long> materialIds);

	@Query("SELECT g.material.id, COALESCE(AVG(g.value), 0.00) " +
			"FROM Grade g " +
			"WHERE g.material.id IN :materialIds " +
			"GROUP BY g.material.id")
	Set<Object[]> findAverageGradeByMaterialsIds(List<Long> materialIds);

	@Query("SELECT g FROM Grade g WHERE g.material.id = :materialId AND g.createdDate BETWEEN :from AND :to")
	Page<Grade> findGradesByMaterialInDateRange(Long materialId, LocalDateTime from, LocalDateTime to, Pageable pageable);
}
