package pl.sknikod.kodemy.infrastructure.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemy.infrastructure.common.entity.Grade;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findAllByMaterialId(Long id);

    @Query("SELECT COALESCE(AVG(g.value), 0.00) FROM Grade g WHERE g.material.id = :materialId")
    Double findAverageGradeByMaterialId(Long materialId);

    @Query("SELECT g.material.id, COALESCE(AVG(g.value), 0.00) " +
            "FROM Grade g " +
            "WHERE g.material.id IN :materialIds " +
            "GROUP BY g.material.id")
    Set<Object[]> findAverageGradeByMaterialsIds(List<Long> materialIds);

    @Query("SELECT m FROM Grade m WHERE m.createdDate BETWEEN :from AND :to AND m.material.id = :materialId")
    Page<Grade> findGradesByMaterialInDateRangeWithPage(Long materialId, Date from, Date to, Pageable pageable);
}
