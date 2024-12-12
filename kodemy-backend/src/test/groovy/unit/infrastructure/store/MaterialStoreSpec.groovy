package unit.infrastructure.store

import io.vavr.Tuple
import io.vavr.Tuple2
import io.vavr.control.Try
import org.spockframework.util.Assert
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import pl.sknikod.kodemybackend.infrastructure.database.Material
import pl.sknikod.kodemybackend.infrastructure.database.MaterialRepository
import pl.sknikod.kodemybackend.infrastructure.store.GradeStore
import pl.sknikod.kodemybackend.infrastructure.store.MaterialStore
import pl.sknikod.kodemybackend.infrastructure.store.UserStore
import pl.sknikod.kodemycommons.exception.NotFound404Exception
import spock.lang.Specification

import java.time.LocalDateTime

class MaterialStoreSpec extends Specification {
    def materialRepository = Mock(MaterialRepository)
    def userStore = Mock(UserStore)
    def gradeStore = Mock(GradeStore)

    def material = new Material()
    def user = new UserStore.User()

    def materialStore = new MaterialStore(materialRepository, userStore, gradeStore)

    def "shouldFindById"() {
        given:
        material.setId(1L)
        material.setUserId(1L)
        user.setId(1L)
        user.setUsername("name")
        materialRepository.findById(1L) >> Optional.of(material)
        userStore.findById(1L) >> Try.success(user)
        gradeStore.findAvgGradeByMaterial(1L) >> Try.success(4.0D)
        gradeStore.getGradeStats(1L) >> Try.success(new ArrayList<>(List.of(1L, 2L, 3L, 4L, 5L)))

        when:
        def result1 = materialStore.findById(1L, false).get()
        def result2 = materialStore.findById(1L, true).get()
        def result3 = materialStore.findById(1L).get()

        then:
        Assert.that(result1 instanceof MaterialStore.FindByIdObject)
        Assert.that(result2.username() == null)
        Assert.that(result3 instanceof MaterialStore.FindByIdObject)
    }

    def "shouldThrowWhenFindById"() {
        given:
        materialRepository.findById(1L) >> Optional.empty()

        when:
        materialStore.findById(1L).get()

        then:
        thrown(NotFound404Exception)
    }

    def "shouldSave"() {
        given:
        materialRepository.save(material) >> material

        when:
        def result = materialStore.save(material).get()

        then:
        Assert.that(result == material)
    }

    def "shouldUpdate"() {
        given:
        materialRepository.save(material) >> material

        when:
        def result = materialStore.update(material).get()

        then:
        Assert.that(result == material)
    }

    def "shouldFindAllWithUser"() {
        given:
        materialRepository.searchMaterialsWithAvgGrades(
                _ as Long,
                _ as String,
                _ as List,
                _ as Long,
                _ as List<Long>,
                _ as List<Long>,
                _ as Long,
                null,
                null,
                _ as Double,
                _ as Double,
                _ as PageRequest
        ) >> new PageImpl<Object[]>(new Object[]{new Object[]{material, 3.0D}} as List<Object[]>)
        userStore.findById(1L) >> Try.success(user)

        when:
        def result = materialStore.findAll(1L, new MaterialStore.FindAllFilters(
                "", 1L, new ArrayList<Material.MaterialStatus>(), 1L, List.of(1L),
                List.of(1L), 3.0D, 3.0D,
                PageRequest.of(1, 1)
        )).get()

        then:
        Assert.that(result instanceof Tuple2<org.springframework.data.domain.Page<MaterialStore.FindAllPageObject>, UserStore.User>)
    }

    def "shouldFindAll"() {
        given:
        materialRepository.searchMaterialsWithAvgGrades(
                _ as Long,
                _ as String,
                _ as List,
                _ as Long,
                _ as List<Long>,
                _ as List<Long>,
                null,
                null,
                null,
                _ as Double,
                _ as Double,
                _ as PageRequest
        ) >> new PageImpl<Object[]>(new Object[]{new Object[]{material, 3.0D}} as List<Object[]>)
        userStore.findUsersById(_ as Set) >> Try.success(new ArrayList<>(List.of(user)))

        when:
        def result = materialStore.findAll(new MaterialStore.FindAllFilters(
                "", 1L, new ArrayList<Material.MaterialStatus>(), 1L, List.of(1L),
                List.of(1L), 3.0D, 3.0D,
                PageRequest.of(1, 1)
        )).get()

        then:
        Assert.that(result instanceof Tuple2<org.springframework.data.domain.Page<MaterialStore.FindAllPageObject>, UserStore.User>)
    }

    def "shouldFindAllInDataRange"() {
        given:
        user.username = "name"
        user.id = 1L
        materialRepository.findMaterialsInDateRangeWithPage(
                _ as LocalDateTime,
                _ as LocalDateTime,
                _ as PageRequest
        ) >> new PageImpl<Material>(List.of(material))
        gradeStore.findAvgGradeByMaterialsIds(_ as Collection)
                >> Try.success(new ArrayList<>(List.of(new GradeStore.FindAvgGradeObject(
                new Object[]{1L, 3.0D}
        ))))
        userStore.findUsersById(_ as Set) >> Try.success(new ArrayList<>(List.of(user)))

        when:
        def result = materialStore.findAllInDateRange(
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                PageRequest.of(1, 1)
        ).get()

        then:
        Assert.that(result.content.size() == 1)
    }

    def "shouldChangeStatus"() {
        given:
        materialRepository.updateStatus(1L, Material.MaterialStatus.APPROVED) >> _

        when:
        def result = materialStore.changeStatus(1L, Material.MaterialStatus.APPROVED).get()

        then:
        Assert.that(result == Tuple.of(1L, Material.MaterialStatus.APPROVED))
    }
}
