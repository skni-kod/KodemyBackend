package pl.sknikod.kodemybackend.infrastructure.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.material.rest.SearchFields;

import java.util.Date;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    @Query(
            value = "SELECT m FROM Material m WHERE m.createdDate BETWEEN :from AND :to",
            countQuery = "SELECT COUNT(m) FROM Material m WHERE m.createdDate BETWEEN :from AND :to")
    Page<Material> findMaterialsInDateRangeWithPage(Date from, Date to, Pageable pageable);

    @Query("SELECT m FROM Material m WHERE " +
            "(:#{#searchFields.id} IS NULL OR m.id = :#{#searchFields.id}) AND " +
            "(:#{#searchFields.title} IS NULL OR m.title >= :#{#searchFields.title}) AND " +
            "(:#{#searchFields.createdBy} IS NULL OR m.author.username <= :#{#searchFields.createdBy})")
    Page<Material> searchMaterials(SearchFields searchFields, PageRequest pageRequest);
}