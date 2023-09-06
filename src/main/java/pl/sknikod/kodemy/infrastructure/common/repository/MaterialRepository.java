package pl.sknikod.kodemy.infrastructure.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemy.infrastructure.common.entity.Material;

import java.util.Date;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    @Query("SELECT m FROM Material m WHERE m.createdDate BETWEEN :from AND :to")
    @EntityGraph(type = EntityGraph.EntityGraphType.LOAD, attributePaths = {"category", "user"})
    Page<Material> findMaterialsInDateRangeWithPage(Date from, Date to, Pageable pageable);
}