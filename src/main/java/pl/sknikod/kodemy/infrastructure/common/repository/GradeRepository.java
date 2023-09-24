package pl.sknikod.kodemy.infrastructure.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemy.infrastructure.common.entity.Grade;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findAllByMaterialId(Long id);

    @Query("SELECT AVG(g.value) FROM Grade g WHERE g.material.id = :materialId")
    Double findAverageGradeByMaterialId(Long materialId);
}
