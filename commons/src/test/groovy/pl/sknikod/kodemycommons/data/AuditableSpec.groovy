package pl.sknikod.kodemycommons.data

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import pl.sknikod.kodemycommons.security.UserPrincipal
import spock.lang.Specification

import java.time.LocalDateTime


class AuditableSpec extends Specification {


    def "should update createdDate and createdBy on prePersist"() {
        given: "fake authentication"
            withAuthentication()

        and: "an instance of Auditable and initial state"
            def auditable = new Auditable() {}
            def beforeDate = auditable.getCreatedDate()
            def beforeCreatedBy = auditable.getCreatedBy()
            def dateBeforePrePersist = LocalDateTime.now()

        when: "onPrePersist is called"
            auditable.onPrePersist()

        then: "before call createdDate and createdBy are null"
            beforeDate == null
            beforeCreatedBy == null

        and: "after call createdDate and createdBy are set"
            auditable.getCreatedDate() != beforeDate
            auditable.getCreatedDate().isAfter(dateBeforePrePersist) || auditable.getCreatedDate().isEqual(dateBeforePrePersist)
            auditable.getCreatedBy() != null

        cleanup:
            clearAuthentication()
    }

    def "should update modifiedDate and modifiedBy on preUpdate"() {
        given: "fake authentication"
            withAuthentication()

        and: "an instance of Auditable and initial state"
            def auditable = new Auditable() {}
            def beforeDate = auditable.getModifiedDate()
            def beforeModifiedBy = auditable.getModifiedBy()
            def dateBeforePreUpdate = LocalDateTime.now()

        when: "onPreUpdate is called"
            auditable.onPreUpdate()

        then: "before call modifiedDate and modifiedBy are null"
            beforeDate == null
            beforeModifiedBy == null

        and: "after call modifiedDate and modifiedBy are set"
            auditable.getModifiedDate() != beforeDate
            auditable.getModifiedDate().isAfter(dateBeforePreUpdate) || auditable.getModifiedDate().isEqual(dateBeforePreUpdate)
            auditable.getModifiedBy() != null

        cleanup:
            clearAuthentication()
    }


    private void withAuthentication() {
        def userPrincipal = new UserPrincipal(1L, "username", Collections.emptyList())
        def authentication = Mock(Authentication) {
            isAuthenticated() >> true
            getPrincipal() >> userPrincipal
        }
        SecurityContextHolder.getContext().setAuthentication(authentication)
    }

    private static void clearAuthentication() {
        SecurityContextHolder.clearContext()
    }

}
