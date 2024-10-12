package pl.sknikod.kodemybackend.infrastructure.database;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    @Query(
            value = "SELECT m FROM Material m LEFT JOIN FETCH m.tags WHERE m.createdDate BETWEEN :from AND :to",
            countQuery = "SELECT COUNT(m) FROM Material m WHERE m.createdDate BETWEEN :from AND :to")
    Page<Material> findMaterialsInDateRangeWithPage(LocalDateTime from, LocalDateTime to, Pageable pageable);

    @Query("SELECT m, COALESCE(AVG(g.value), 0.0) " +
            "FROM Material m " +
            "LEFT JOIN Grade g ON m.id = g.material.id " +
            "WHERE (:userId IS NULL OR m.userId = :userId) " +
            "AND (:id IS NULL OR m.id = :id) " +
            "AND (:phrase IS NULL OR m.title LIKE CONCAT('%', :phrase, '%')) " +
            "AND ((:statuses) IS NULL OR m.status IN (:statuses)) " +
            "AND (:createdBy IS NULL OR m.createdBy = :createdBy) " +
            "AND (:sectionId IS NULL OR m.category.id IN (" +
            "   SELECT c.id FROM Category c WHERE c.section.id = :sectionId)) " +
            "AND (:categoryIds IS NULL OR m.category.id IN :categoryIds) " +
            "AND (:tagIds IS NULL OR EXISTS (" +
            "   SELECT 1 FROM m.tags t WHERE t.id IN :tagIds)) " +
            "AND (cast(:dateFrom as date) IS NULL OR m.createdDate >= :dateFrom) " +
            "AND (cast(:dateTo as date) IS NULL OR m.createdDate <= :dateTo) " +
            "GROUP BY m " +
            "HAVING (:minAvgGrade IS NULL OR :minAvgGrade <= COALESCE(AVG(g.value), 0.00)) " +
            "AND (:maxAvgGrade IS NULL OR :maxAvgGrade >= COALESCE(AVG(g.value), 0.00))")
    Page<Object[]> searchMaterialsWithAvgGrades(
            Long id,
            String phrase,
            List<Material.MaterialStatus> statuses,
            String createdBy,
            Long sectionId,
            List<Long> categoryIds,
            List<Long> tagIds,
            Long userId,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            Double minAvgGrade,
            Double maxAvgGrade,
            Pageable pageable
    );
}