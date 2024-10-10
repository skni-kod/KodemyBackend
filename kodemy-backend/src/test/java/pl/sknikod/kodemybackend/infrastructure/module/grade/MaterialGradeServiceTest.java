package pl.sknikod.kodemybackend.infrastructure.module.grade;

import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.sknikod.kodemybackend.factory.GradeFactory;
import pl.sknikod.kodemybackend.factory.MaterialFactory;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.GradeMapper;
import pl.sknikod.kodemybackend.infrastructure.database.Grade;
import pl.sknikod.kodemybackend.infrastructure.database.Material;
import pl.sknikod.kodemybackend.infrastructure.dao.GradeDao;
import pl.sknikod.kodemybackend.infrastructure.dao.MaterialDao;
import pl.sknikod.kodemybackend.infrastructure.module.grade.model.GradeMaterialFilterSearchParams;
import pl.sknikod.kodemybackend.BaseTest;
import pl.sknikod.kodemybackend.WithUserPrincipal;
import pl.sknikod.kodemycommons.exception.InternalError500Exception;
import pl.sknikod.kodemycommons.exception.NotFound404Exception;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class MaterialGradeServiceTest extends BaseTest {
    final MaterialGradeService materialGradeService = new MaterialGradeService(
            new TestMaterialDao(), new GradeMapperImpl(), new TestGradeDao()
    );

    static final MaterialGradeService.MaterialAddGradeRequest request = new MaterialGradeService.MaterialAddGradeRequest();

    @Test
    @WithUserPrincipal
    void addGrade_shouldSucceed() {
        // given
        request.setGrade("1");
        // when
        var result = materialGradeService.addGrade(1L, request);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals(1L, result.getMaterial().getId()),
                () -> assertEquals(Double.valueOf(request.getGrade()), result.getValue())
        );
    }

    @Test
    void addGrade_shouldThrowException_whenNotAuthorize() {
        // given
        // when & then
        assertThrows(InternalError500Exception.class, () -> materialGradeService.addGrade(1L, request));
    }

    @Test
    @WithUserPrincipal
    void addGrade_shouldThrowException_whenMaterialNotFound() {
        // given
        // when & then
        assertThrows(NotFound404Exception.class, () -> materialGradeService.addGrade(2L, request));
    }

    @Test
    @WithUserPrincipal
    void addGrade_shouldThrowException_whenSaveFails() {
        // given
        // when & then
        assertThrows(RuntimeException.class, () -> materialGradeService.addGrade(3L, request));
    }
    
    @Test
    void showGrades_shouldSuccess(){
        //given
        //when
        var result = materialGradeService.showGrades(PageRequest.of(1,1), new GradeMaterialFilterSearchParams(), 1L);
        //then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void showGrades_shouldThrowException_whenRepoError(){
        //given
        //when & then
        assertThrows(RuntimeException.class, () -> materialGradeService.showGrades(PageRequest.of(1,1), new GradeMaterialFilterSearchParams(), 2L));
    }

    static class TestGradeDao extends GradeDao {
        public TestGradeDao() {
            super(null);
        }

        @Override
        public Try<Grade> save(Grade grade) {
            if (grade.getMaterial().getId() == 1L) {
                grade.setId(1L);
                return Try.success(grade);
            }
            throw new RuntimeException();
        }

        @Override
        public Try<Page<Grade>> findGradesByMaterialInDateRange(Long materialId, Date minDate, Date maxDate, PageRequest pageRequest) {
            if (materialId == 1L)
                return Try.success(new PageImpl<>(
                        List.of(GradeFactory.grade(1L, "1", materialId)),
                        Pageable.ofSize(pageRequest.getPageSize()),
                        1
                ));
            throw new RuntimeException();
        }
    }

    static class TestMaterialDao extends MaterialDao {
        public TestMaterialDao() {
            super(null);
        }

        @Override
        public Try<Material> findById(Long id) {
            if (id == 1L || id == 3L)
                return Try.success(MaterialFactory.material(id, Material.MaterialStatus.APPROVED));
            return Try.failure(new NotFound404Exception(""));
        }
    }

    static class GradeMapperImpl implements GradeMapper {
        @Override
        public MaterialGradeService.GradePageable map(Grade grade) {
            return new MaterialGradeService.GradePageable(grade.getId(), null, null);
        }

        @Override
        public Set<MaterialGradeService.GradePageable> map(Set<Grade> grades) {
            return grades.stream().map(this::map).collect(Collectors.toSet());
        }
    }
}