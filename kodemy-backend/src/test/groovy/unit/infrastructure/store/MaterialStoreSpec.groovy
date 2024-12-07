package unit.infrastructure.store

import io.vavr.Tuple
import io.vavr.Tuple2
import io.vavr.control.Try
import jakarta.transaction.Status
import org.apache.catalina.User
import org.hibernate.query.Page
import org.spockframework.util.Assert
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import pl.sknikod.kodemybackend.infrastructure.database.Grade
import pl.sknikod.kodemybackend.infrastructure.database.Material
import pl.sknikod.kodemybackend.infrastructure.database.MaterialRepository
import pl.sknikod.kodemybackend.infrastructure.store.GradeStore
import pl.sknikod.kodemybackend.infrastructure.store.MaterialStore
import pl.sknikod.kodemybackend.infrastructure.store.UserStore
import pl.sknikod.kodemycommons.exception.NotFound404Exception
import spock.lang.Specification

class MaterialStoreSpec extends Specification{
    def materialRepository = Mock(MaterialRepository)
    def userStore = Mock(UserStore)
    def gradeStore = Mock(GradeStore)
    
    def material = new Material()
    def user = new UserStore.User()
    
    def materialStore = new MaterialStore(materialRepository,userStore,gradeStore)
    
    def "shouldFindById"(){
        given:
        material.setId(1L)
        material.setUserId(1L)
        user.setId(1L)
        user.setUsername("name")
        materialRepository.findById(1L) >> Optional.of(material)
        userStore.findById(1L) >> Try.success(user)
        gradeStore.findAvgGradeByMaterial(1L) >> Try.success(4.0D)
        gradeStore.getGradeStats(1L) >> Try.success(new ArrayList<>(List.of(1L,2L,3L,4L,5L)))
        when:
        def result1 = materialStore.findById(1L, false).get()
        def result2 = materialStore.findById(1L, true).get()
        def result3 = materialStore.findById(1L).get()
        then:
        Assert.that(result1 instanceof MaterialStore.FindByIdObject)
        Assert.that(result2.username()==null)
        Assert.that(result3 instanceof MaterialStore.FindByIdObject)
    }
    
    def "shouldThrowWhenFindById"(){
        given:
        materialRepository.findById(1L) >> Optional.empty()
        when:
        materialStore.findById(1L).get()
        then:
        thrown(NotFound404Exception)
    }
    
    def "shouldSave"(){
        given:
        materialRepository.save(material) >> material
        when:
        def result = materialStore.save(material).get()
        then:
        Assert.that(result == material)
    }
    
    def "shouldUpdate"(){
        given:
        materialRepository.save(material) >> material
        when:
        def result = materialStore.update(material).get()
        then:
        Assert.that(result == material)
    }
    
    def "shouldChangeStatus"(){
        given:
        materialRepository.updateStatus(1L, Material.MaterialStatus.APPROVED) >> _
        when:
        def result = materialStore.changeStatus(1L, Material.MaterialStatus.APPROVED).get()
        then:
        Assert.that(result == Tuple.of(1L, Material.MaterialStatus.APPROVED))
    }
}
