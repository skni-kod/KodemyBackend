package unit.infrastructure.module.grade

import io.vavr.Tuple2
import io.vavr.control.Try
import org.spockframework.util.Assert
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import pl.sknikod.kodemybackend.infrastructure.database.Grade
import pl.sknikod.kodemybackend.infrastructure.mapper.GradeMapper
import pl.sknikod.kodemybackend.infrastructure.module.grade.GradeService
import pl.sknikod.kodemybackend.infrastructure.module.grade.model.GradeMaterialFilterSearchParams
import pl.sknikod.kodemybackend.infrastructure.store.GradeStore
import pl.sknikod.kodemybackend.infrastructure.store.UserStore
import spock.lang.Specification

class GradeServiceSpec extends Specification {
    def gradeMapper = Mock(GradeMapper)
    def gradeStore = Mock(GradeStore)

    def gradeService = new GradeService(gradeMapper, gradeStore)

    def "shouldAddGrade"() {
        given:
        gradeStore.addGrade(1L, 3.0D) >> _

        when:
        def request = new GradeService.MaterialAddGradeRequest()
        request.grade = "3.0"
        gradeService.addGrade(1L, request)

        then:
        noExceptionThrown()
    }

    def "shouldShowGrades"() {
        given:
        def grade = new Grade()
        def user = new UserStore.User()
        user.id = 1L
        user.username = "name"
        gradeStore.findGradesByMaterialInDateRange(1L, _ as Date, _ as Date, _ as PageRequest)
                >> Try.success(new Tuple2(new PageImpl(List.of(grade)), new HashSet<>(Set.of(user))))
        gradeMapper.map(grade, _ as String)

        when:
        def result = gradeService.showGrades(
                PageRequest.of(1, 1), new GradeMaterialFilterSearchParams(), 1L)

        then:
        Assert.that(result.content.size() == 1)
    }
}
