package pl.sknikod.kodemycommons.security

import org.springframework.security.core.authority.SimpleGrantedAuthority
import spock.lang.Specification

class JwtProviderSpec extends Specification {

    def "should generate delegation token"() {
        given:
            def jwtProvider = new JwtProvider(new FakeProperties())
            String subject = "user123"
            String authority = "ROLE_ADMIN"
        when:
            def token = jwtProvider.generateDelegationToken(subject, authority)
        then:
            token != null
            token.id() != null
            token.value() != null
            token.expiration() > new Date()
        and:
            def parsed = jwtProvider.parseToken(token.value())
            parsed.isSuccess()
            def result = parsed.get()
            result.bearerId == token.id()
            result.username == "user123"
            result.authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
    }

    def "should generate user token"() {
        given:
            def jwtProvider = new JwtProvider(new FakeProperties())
            def input = new JwtProvider.Input(5L, "user123", false, false,
                    false, true, [new SimpleGrantedAuthority("ROLE_USER")] as Set)
        when:
            def token = jwtProvider.generateUserToken(input)
        then:
            token != null
            token.id() != null
            token.value() != null
            token.expiration() > new Date()
        and:
            def parsed = jwtProvider.parseToken(token.value())
            parsed.isSuccess()
            def result = parsed.get()
            result.id == 5
            result.bearerId == token.id()
            result.username == "user123"
            result.authorities.contains(new SimpleGrantedAuthority("ROLE_USER"))
    }



    static class FakeProperties extends JwtProvider.Properties {
        FakeProperties() {
            secretKey = 'YWJjZGVmZ2hjvbwjrW5vcHFyc3R1dnd4eXoxMjM0NTY3OnDMTIzNDU2Nzg5MDEyMzQ1Njc4OTAf='
            bearerExpirationMin = 15
            delegationExpirationMin = 60
        }
    }
}
