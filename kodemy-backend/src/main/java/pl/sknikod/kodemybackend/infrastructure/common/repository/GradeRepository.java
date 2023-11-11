package pl.sknikod.kodemybackend.infrastructure.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Grade;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    Date DATE_MIN = java.sql.Date.valueOf(LocalDate.of(2023,1,1));
    Date DATE_MAX = java.sql.Date.valueOf(LocalDate.of(9999, 12, 31));

    List<Grade> findAllByMaterialId(Long id);
    Long countAllByMaterialIdAndValue(Long id, Double value);

    @Query("SELECT COALESCE(AVG(g.value), 0.00) FROM Grade g WHERE g.material.id = :materialId")
    Double findAverageGradeByMaterialId(Long materialId);

    @Query("SELECT g.material.id, COALESCE(AVG(g.value), 0.00) " +
            "FROM Grade g " +
            "WHERE g.material.id IN :materialIds " +
            "GROUP BY g.material.id")
    Set<Object[]> findAverageGradeByMaterialsIds(List<Long> materialIds);

    @Query("SELECT g FROM Grade g WHERE g.material.id = :materialId AND g.createdDate BETWEEN :from AND :to")
    Page<Grade> findGradesByMaterialInDateRange(Long materialId, Date from, Date to, Pageable pageable);
}
