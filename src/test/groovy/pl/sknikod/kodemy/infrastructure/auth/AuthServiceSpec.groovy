package pl.sknikod.kodemy.infrastructure.auth

import org.springframework.beans.factory.annotation.Autowired
import pl.sknikod.kodemy.MvcIntegrationSpec
import pl.sknikod.kodemy.infrastructure.common.entity.UserProviderType
import pl.sknikod.kodemy.infrastructure.user.UserPrincipalUseCase

import javax.servlet.http.HttpServletRequest

class AuthServiceSpec extends MvcIntegrationSpec {

    private static final String DOMAIN = "http://localhost:8181"

    @Autowired
    UserPrincipalUseCase userPrincipalUseCase

    def authService = new AuthService(userPrincipalUseCase)

    def "should return url for authorization"() {
        given:
        HttpServletRequest request = Mock(HttpServletRequest)
        request.getScheme() >> "http"
        request.getServerName() >> "localhost"
        request.getServerPort() >> 8181
        when:
        String link = authService.getLink(request, {}, DOMAIN)

        then:
        link == "${DOMAIN}?redirect_uri=${DOMAIN}"
    }

    def "should return list of UserProviderType values"() {
        when:
        def result = authService.getProvidersList()

        then:
        result == UserProviderType.values()
    }
}
