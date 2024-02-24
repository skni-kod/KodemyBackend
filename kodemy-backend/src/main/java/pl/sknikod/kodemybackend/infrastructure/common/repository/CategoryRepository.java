package pl.sknikod.kodemybackend.infrastructure.common.repository;

import io.vavr.control.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemybackend.exception.structure.NotFoundException;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    default Category findCategoryById(Long categoryId) {
        return Option.ofOptional(findById(categoryId))
                .getOrElseThrow(() ->
                        new NotFoundException(NotFoundException.Format.ENTITY_ID, Category.class, categoryId)
                );
    }
}
