package pl.sknikod.kodemyauth.infrastructure.module.oauth2.exchange.github

import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

class GithubExchangeSpec extends Specification {
    def restTemplate = Mock(RestTemplate)

    def githubExchangeFlow = new GithubExchange(restTemplate)

    def "should get successfully provider user"() {
        given:
        def code = "code"
        def registrationId = githubExchangeFlow.registration.toString()
        def repository = Mock(ClientRegistrationRepository)
        def client = Mock(ClientRegistration) {
            getRegistrationId() >> registrationId
            getClientId() >> "clientId"
            getClientSecret() >> "clientSecret"
            getProviderDetails() >> Mock(ClientRegistration.ProviderDetails) {
                getTokenUri() >> "tokenUri"
                getUserInfoEndpoint() >> Mock(ClientRegistration.ProviderDetails.UserInfoEndpoint) {
                    getUri() >> "userInfoEndpoint"
                }
            }
        }
        repository.findByRegistrationId(registrationId) >> client

        restTemplate.exchange("tokenUri", HttpMethod.POST, _ as HttpEntity, _ as ParameterizedTypeReference, _ as Object[])
                >> ResponseEntity.ok(Map.of("access_token", "access_token"))

        restTemplate.exchange("userInfoEndpoint", HttpMethod.GET, _ as HttpEntity, _ as ParameterizedTypeReference, _ as Object[])
                >> ResponseEntity.ok(Map.of("login", "login", "email", ""))

        def email = new GithubExchange.Email()
        email.setEmail("email@email.com")
        email.setPrimary(true)

        restTemplate.exchange("userInfoEndpoint/emails", HttpMethod.GET, _ as HttpEntity, _ as ParameterizedTypeReference, _ as Object[])
                >> ResponseEntity.ok([email])

        when:
        def result = githubExchangeFlow.exchange(repository, code)

        then:
        verifyAll {
            result.username == "login"
            result.email == "email@email.com"
        }
    }
}
