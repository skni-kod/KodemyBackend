package unit.infrastructure.store

import org.spockframework.util.Assert
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import pl.sknikod.kodemybackend.infrastructure.store.UserStore
import pl.sknikod.kodemycommons.exception.InternalError500Exception
import pl.sknikod.kodemycommons.network.LanRestTemplate
import spock.lang.Specification

import java.util.stream.Stream

class UserStoreSpec extends Specification {
    def lanRestTemplate = Mock(LanRestTemplate)
    def authRouteBaseUrl = "http://localhost:8081"

    def user = new UserStore.User()

    def userStore = new UserStore(lanRestTemplate, authRouteBaseUrl)

    def "shouldFindUserById"() {
        given:
        lanRestTemplate.exchange(_ as String, HttpMethod.GET, null, _ as ParameterizedTypeReference)
                >> ResponseEntity.of(Optional.of(new ArrayList<>(List.of(user))))

        when:
        def result = userStore.findUsersById(Stream.of(1L)).get()

        then:
        Assert.that(result == List.of(user))
    }

    def "shouldThrowWhenFindUserById"() {
        given:
        lanRestTemplate.exchange(_ as String, HttpMethod.GET, null, _ as ParameterizedTypeReference)
                >> ResponseEntity.internalServerError()

        when:
        userStore.findUsersById(Stream.of(1L)).get()

        then:
        thrown(InternalError500Exception)
    }

    def "shouldFindUsersByIdList"() {
        given:
        lanRestTemplate.exchange(_ as String, HttpMethod.GET, null, _ as ParameterizedTypeReference)
                >> ResponseEntity.of(Optional.of(new ArrayList<>(List.of(user))))

        when:
        def result = userStore.findUsersById(List.of(1L)).get()

        then:
        Assert.that(result == List.of(user))
    }

    def "shouldThrowWhenFindUserByIdList"() {
        given:
        lanRestTemplate.exchange(_ as String, HttpMethod.GET, null, _ as ParameterizedTypeReference)
                >> ResponseEntity.internalServerError()

        when:
        userStore.findUsersById(List.of(1L)).get()

        then:
        thrown(InternalError500Exception)
    }

    def "shouldFindById"() {
        given:
        lanRestTemplate.exchange(_ as String, HttpMethod.GET, null, _ as ParameterizedTypeReference)
                >> ResponseEntity.of(Optional.of(new ArrayList<>(List.of(user))))

        when:
        def result = userStore.findById(1L).get()

        then:
        Assert.that(result == user)
    }

    def "shouldThrowWhenFindById"() {
        given:
        lanRestTemplate.exchange(_ as String, HttpMethod.GET, null, _ as ParameterizedTypeReference)
                >> ResponseEntity.internalServerError()

        when:
        userStore.findById(1L).get()

        then:
        thrown(InternalError500Exception)
    }
}
