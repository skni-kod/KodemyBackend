package unit.infrastructure.module.grade

import org.spockframework.util.Assert
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import pl.sknikod.kodemybackend.infrastructure.module.grade.GradeController
import pl.sknikod.kodemybackend.infrastructure.module.grade.GradeService
import pl.sknikod.kodemybackend.infrastructure.module.grade.model.GradeMaterialFilterSearchParams
import pl.sknikod.kodemybackend.infrastructure.module.grade.model.GradeMaterialSortField
import spock.lang.Specification

class GradeControllerSpec extends Specification {
    def gradeService = Mock(GradeService)

    def gradeController = new GradeController(gradeService)

    def "shouldAddGrade"() {
        given:
        gradeService.addGrade(1L, new GradeService.MaterialAddGradeRequest()) >> _

        when:
        gradeController.addGrade(new GradeService.MaterialAddGradeRequest(), 1L)

        then:
        noExceptionThrown()
    }

    def "shouldShowGrades"() {
        given:
        gradeService.showGrades(_ as PageRequest, _ as GradeMaterialFilterSearchParams, 1L)
                >> new PageImpl<>(List.of(new GradeService.GradePageable(
                1L, 3.0D,
                new GradeService.GradePageable.AuthorDetails(1L, "name")
        )))

        when:
        def result = gradeController.showGrades(
                1, 1, 1L, GradeMaterialSortField.VALUE, Sort.Direction.ASC,
                new GradeMaterialFilterSearchParams()
        )

        then:
        Assert.that(result.body.content.size() == 1)
    }
}
