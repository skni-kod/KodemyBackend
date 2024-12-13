package pl.sknikod.kodemycommons.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification

class AuthFacadeSpec extends Specification {

    def setup() {
        clearAuthentication()
    }

    def cleanup() {
        clearAuthentication()
    }

    def "should get authentication"() {
        given:
            withAuthentication()
        when:
            def authentication = AuthFacade.getAuthentication()
        then:
            authentication.isPresent()
    }

    def "should check is authenticated"() {
        given:
           auth.call()
        when:
            def isAuthenticated = AuthFacade.isAuthenticated()
        then:
            isAuthenticated == expected

        where:
            auth                                          || expected ;
            {withAuthentication(authenticated: true)}     || true     ;
            {withAuthentication(authenticated: false)}    || false    ;
    }

    def "should get current username"() {
        given:
            withAuthentication(username: "abc")
        when:
            def username = AuthFacade.getCurrentUsername()
        then:
            username == "abc"
    }

    def "should get current user principal"() {
        given:
            withAuthentication()
        when:
            def userPrincipal = AuthFacade.getCurrentUserPrincipal()
        then:
            userPrincipal.isPresent()
    }

    def "should find any authority"() {
        given:
            withAuthentication(authorities: List.of(
                    new SimpleGrantedAuthority("ROLE_USER"),
                    new SimpleGrantedAuthority("ROLE_ADMIN")))
        when:
            def anyAuthority = AuthFacade.hasAnyAuthority("ROLE_SUPERADMIN", "ROLE_ADMIN")
        then:
            anyAuthority
    }

    def "should find authority"() {
        given:
            withAuthentication(authorities: List.of(
                    new SimpleGrantedAuthority("ROLE_USER"),
                    new SimpleGrantedAuthority("ROLE_ADMIN")))
        when:
            def authority = AuthFacade.hasAuthority(role)
        then:
            authority == expected

        where:
            role               || expected
            "ROLE_ADMIN"       || true
            "ROLE_SUPERADMIN"  || false
    }


    def withAuthentication(Map args = [:]) {
        Long id = args.containsKey('id') ? args.id : 1L
        String username = args.containsKey('username') ? args.username : "username"
        Collection<? extends GrantedAuthority> authorities = args.containsKey('authorities') ? args.authorities : Collections.emptyList()
        boolean authenticated = args.containsKey('authenticated') ? args.authenticated : true

        def userPrincipal = new UserPrincipal(id, username, authorities as Collection<SimpleGrantedAuthority>)
        def authentication = Mock(Authentication) {
            isAuthenticated() >> authenticated
            getPrincipal() >> userPrincipal
            getName() >> username
            getAuthorities() >> authorities
        }
        SecurityContextHolder.getContext().setAuthentication(authentication)
    }

    private static void clearAuthentication() {
        SecurityContextHolder.clearContext()
    }
}
