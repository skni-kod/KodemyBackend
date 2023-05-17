package pl.sknikod.kodemy.category;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.exception.general.NotFoundException;
import pl.sknikod.kodemy.exception.general.ServerProcessingException;
import pl.sknikod.kodemy.rest.mapper.CategoryMaterialMapper;
import pl.sknikod.kodemy.rest.response.SingleMaterialResponse;

import java.util.Set;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMaterialMapper materialMapper;

    public Set<SingleMaterialResponse> showMaterials(Long categoryId) {
        return Option.of(
                        Option.ofOptional(categoryRepository.findById(categoryId))
                                .getOrElseThrow(() -> new NotFoundException(NotFoundException.Format.entityId, Category.class, categoryId)))
                .map(Category::getMaterials)
                .map(materialMapper::map)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.processFailed, Category.class));
    }
}
