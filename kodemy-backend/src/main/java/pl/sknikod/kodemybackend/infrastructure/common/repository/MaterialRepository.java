package pl.sknikod.kodemybackend.infrastructure.common.repository;

import io.vavr.control.Option;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemybackend.exception.structure.NotFoundException;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;

import java.util.Date;
import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    @Query(
            value = "SELECT m FROM Material m LEFT JOIN FETCH m.technologies WHERE m.createdDate BETWEEN :from AND :to",
            countQuery = "SELECT COUNT(m) FROM Material m WHERE m.createdDate BETWEEN :from AND :to")
    Page<Material> findMaterialsInDateRangeWithPage(Date from, Date to, Pageable pageable);

    @Query("SELECT m, COALESCE(AVG(g.value), 0.0) " +
            "FROM Material m " +
            "LEFT JOIN Grade g ON m.id = g.material.id " +
            "WHERE (:authorId IS NULL OR m.author.id = :authorId) " +
            "AND (:id IS NULL OR m.id = :id) " +
            "AND (:phrase IS NULL OR m.title LIKE CONCAT('%', :phrase, '%')) " +
            "AND (:status IS NULL OR m.status = :status) " +
            "AND (:createdBy IS NULL OR m.createdBy = :createdBy) " +
            "AND (:sectionId IS NULL OR m.category.id IN (" +
            "   SELECT c.id FROM Category c WHERE c.section.id = :sectionId)) " +
            "AND (:categoryIds IS NULL OR m.category.id IN :categoryIds) " +
            "AND (:technologyIds IS NULL OR EXISTS (" +
            "   SELECT 1 FROM m.technologies t WHERE t.id IN :technologyIds)) " +
            "GROUP BY m")
    Page<Object[]> searchMaterialsWithAvgGrades(
            Long id,
            String phrase,
            String status,
            String createdBy,
            Long sectionId,
            List<Long> categoryIds,
            List<Long> technologyIds,
            Long authorId,
            Pageable pageable
    );

    default Material findMaterialById(Long materialId) {
        return Option.ofOptional(findById(materialId))
                .getOrElseThrow(() ->
                        new NotFoundException(NotFoundException.Format.ENTITY_ID, Material.class, materialId)
                );
    }
}