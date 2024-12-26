package unit.infrastructure.store

import io.vavr.control.Try
import org.spockframework.util.Assert
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.authority.SimpleGrantedAuthority
import pl.sknikod.kodemybackend.infrastructure.database.Grade
import pl.sknikod.kodemybackend.infrastructure.database.GradeRepository
import pl.sknikod.kodemybackend.infrastructure.database.MaterialRepository
import pl.sknikod.kodemybackend.infrastructure.store.GradeStore
import pl.sknikod.kodemybackend.infrastructure.store.UserStore
import pl.sknikod.kodemycommons.exception.AlreadyExists409Exception
import pl.sknikod.kodemycommons.exception.NotFound404Exception
import pl.sknikod.kodemycommons.security.UserPrincipal
import pl.sknikod.kodemycommons.util.PrincipalUtil
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.ZoneId

class GradeStoreSpec extends Specification {
    def gradeRepository = Mock(GradeRepository)
    def userStore = Mock(UserStore)
    def materialRepository = Mock(MaterialRepository)
    def principalUtil = Mock(PrincipalUtil)

    def gradeStore = new GradeStore(gradeRepository, userStore, materialRepository, principalUtil)

    private static final MATERIAL_ID = 1L
    private static final GRADE = 3.0
    private static final USER_ID = 1L

    def "shouldFindAvgGradeByMaterialId"() {
        given:
        gradeRepository.findAvgGradeByMaterialId(MATERIAL_ID) >> GRADE

        when:
        def result = gradeStore.findAvgGradeByMaterial(MATERIAL_ID).get()

        then:
        Assert.that(result == 3.0)
    }

    def "shouldGetGradeStatsByMaterialId"() {
        given:
        gradeRepository.countAllByMaterialIdAndValue(MATERIAL_ID, 1.0)
                >> 0L
        gradeRepository.countAllByMaterialIdAndValue(MATERIAL_ID, 2.0)
                >> 0L
        gradeRepository.countAllByMaterialIdAndValue(MATERIAL_ID, GRADE)
                >> 1L
        gradeRepository.countAllByMaterialIdAndValue(MATERIAL_ID, 4.0)
                >> 0L
        gradeRepository.countAllByMaterialIdAndValue(MATERIAL_ID, 5.0)
                >> 0L

        when:
        def result = gradeStore.getGradeStats(MATERIAL_ID).get()

        then:
        Assert.that(result == List.of(0L, 0L, 1L, 0L, 0L))
    }

    def "shouldSaveGrade"() {
        given:
        def grade = new Grade()
        gradeRepository.save(_ as Grade) >> grade

        when:
        def result = gradeStore.save(grade).get()

        then:
        Assert.that(grade == result)
    }

    def "shouldFindGradesByMaterialInDateRange"() {
        given:
        def grades = List.of(
                new Grade(GRADE, USER_ID, MATERIAL_ID)
        )
        def user = new UserStore.User()
        user.id = USER_ID
        user.username = "name"
        gradeRepository.findGradesByMaterialInDateRange(
                MATERIAL_ID,
                _ as LocalDateTime,
                _ as LocalDateTime,
                _ as PageRequest
        ) >> new PageImpl<Grade>(grades)
        userStore.findUsersById(_ as Set) >> Try.success(new ArrayList<>(List.of(user)))

        when:
        def result = gradeStore.findGradesByMaterialInDateRange(
                MATERIAL_ID,
                Date.from(LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()),
                PageRequest.of(1, 1,)
        ).get()

        then:
        verifyAll(result) {
            result._1() == new PageImpl<Grade>(grades)
            result._2() == Set.of(user)
        }
    }

    def "shouldThrowWhenFindGradesByMaterialInDateRange"() {
        given:
        def grades = List.of(
                new Grade(GRADE, USER_ID, MATERIAL_ID)
        )
        gradeRepository.findGradesByMaterialInDateRange(
                MATERIAL_ID,
                _ as LocalDateTime,
                _ as LocalDateTime,
                _ as PageRequest
        ) >> new PageImpl<Grade>(grades)
        userStore.findUsersById(_ as Set) >> Try.success(new ArrayList<>())

        when:
        gradeStore.findGradesByMaterialInDateRange(
                MATERIAL_ID,
                Date.from(LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()),
                PageRequest.of(1, 1,)
        ).get()

        then:
        thrown(IllegalStateException)
    }

    def "shouldAddGrade"() {
        given:
        def grade = new Grade(GRADE, USER_ID, MATERIAL_ID)
        materialRepository.existsById(MATERIAL_ID) >> true
        gradeRepository.save(_ as Grade) >> grade
        principalUtil.getPrincipal() >> Optional.of(new UserPrincipal(
                USER_ID, "", false, false,
                false, true, new HashSet<SimpleGrantedAuthority>()
        ))

        when:
        def result = gradeStore.addGrade(MATERIAL_ID, GRADE).get()

        then:
        Assert.that(grade == result)
    }

    def "same user cannot add more than 1 grade under the material"() {
        given:
            materialRepository.existsById(MATERIAL_ID) >> true
            principalUtil.getPrincipal() >> Optional.of(new UserPrincipal(
                    USER_ID, "", false, false,
                    false, true, new HashSet<SimpleGrantedAuthority>()
            ))
        and:
            def savedGrades = []
            gradeRepository.save(_ as Grade) >> { Grade g ->
                savedGrades << g
                return g
            }
            gradeRepository.existsByMaterialIdAndUserId(_ as Long, _ as Long) >> { Long materialIdArg, Long userIdArg ->
                savedGrades.any { it.material.id == materialIdArg && it.userId == userIdArg }
            }

        when: "add grade to the same material more than 1 time"
            def firstAdd = gradeStore.addGrade(1, 1)
            def secondAdd = gradeStore.addGrade(1, 2)

        then: "second addition is failure"
            firstAdd.isSuccess()
            secondAdd.isFailure()
            secondAdd.failed().get() instanceof AlreadyExists409Exception

    }

    def "shouldThrowWhenAddGrade"() {
        given:
        materialRepository.existsById(MATERIAL_ID) >> false

        when:
        gradeStore.addGrade(MATERIAL_ID, GRADE).get()

        then:
        thrown(NotFound404Exception)
    }

    def "shouldFindAvgGradeByMaterialsIds"() {
        given:
        gradeRepository.findAvgGradeByMaterialsIds(_ as List<Long>)
                >> new HashSet<Object[]>()

        when:
        def result = gradeStore.findAvgGradeByMaterialsIds(List.of(1L)).get()

        then:
        Assert.that(result in List<GradeStore.FindAvgGradeObject>)
    }
}
