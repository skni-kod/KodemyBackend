package pl.sknikod.kodemygateway.infrastructure.module.oauth2

import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationToken
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2AuthorizationException
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2ErrorCodes
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse
import pl.sknikod.kodemygateway.infrastructure.module.oauth2.model.AuthorizeResponse
import reactor.core.publisher.Mono
import spock.lang.Specification

class ReactiveAuthorizationManagerSpec extends Specification {
    private static final ACCESS_TOKEN
            = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWF0IjoxNTE2MjM5MDIyfQ.L8i6g3PfcHlioHCCPURC9pmXT7gdJpx3kOoyAfNUwCc";
    private static final REFRESH_TOKEN = UUID.randomUUID().toString();

    def authorizeResponseClient = Mock(AuthorizeResponseClient)
    def manager = new ReactiveAuthorizationManager(authorizeResponseClient)

    def "should authenticate"() {
        given:
        def token = Mock(OAuth2AuthorizationCodeAuthenticationToken) {
            getAuthorizationExchange() >> Mock(OAuth2AuthorizationExchange) {
                getAuthorizationResponse() >> Mock(OAuth2AuthorizationResponse) {
                    getCode() >> "code"
                    statusError() >> false
                    getState() >> "state"
                }
                getAuthorizationRequest() >> Mock(OAuth2AuthorizationRequest) {
                    getState() >> "state"
                }
            }
            getClientRegistration() >> Mock(ClientRegistration)
            getName() >> "name"
        }

        def authorizeResponse = Mock(AuthorizeResponse) {
            getAccessToken() >> ACCESS_TOKEN
            getRefreshToken() >> REFRESH_TOKEN
        }

        authorizeResponseClient.getAuthorizeResponse(token) >> Mono.just(authorizeResponse)

        when:
        def result = manager.authenticate(token).block()

        then:
        result instanceof OAuth2LoginAuthenticationToken
        verifyAll((OAuth2LoginAuthenticationToken) result) {
            getAccessToken().getTokenValue() == ACCESS_TOKEN
            getRefreshToken().getTokenValue() == REFRESH_TOKEN
        }
    }

    def "should throw error when statusError is true"() {
        given:
        def error = new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN)
        def token = Mock(OAuth2AuthorizationCodeAuthenticationToken) {
            getAuthorizationExchange() >> Mock(OAuth2AuthorizationExchange) {
                getAuthorizationResponse() >> Mock(OAuth2AuthorizationResponse) {
                    statusError() >> true
                    getError() >> error
                }
            }
        }

        when:
        manager.authenticate(token).block()

        then:
        def ex = thrown(OAuth2AuthorizationException)
        ex.error == error
    }

    def "should throw error when state parameter different"() {
        given:
        def token = Mock(OAuth2AuthorizationCodeAuthenticationToken) {
            getAuthorizationExchange() >> Mock(OAuth2AuthorizationExchange) {
                getAuthorizationResponse() >> Mock(OAuth2AuthorizationResponse) {
                    getState() >> "state"
                    statusError() >> false
                }
                getAuthorizationRequest() >> Mock(OAuth2AuthorizationRequest) {
                    getState() >> "state2"
                }
            }
        }

        when:
        manager.authenticate(token).block()

        then:
        def ex = thrown(OAuth2AuthorizationException)
        ex.error.errorCode == "invalid_state_parameter"
    }

    def "should throw error when reactive client error"() {
        given:
        def token = Mock(OAuth2AuthorizationCodeAuthenticationToken) {
            getAuthorizationExchange() >> Mock(OAuth2AuthorizationExchange) {
                getAuthorizationResponse() >> Mock(OAuth2AuthorizationResponse) {
                    getState() >> "state"
                    statusError() >> false
                }
                getAuthorizationRequest() >> Mock(OAuth2AuthorizationRequest) {
                    getState() >> "state"
                }
            }
        }

        authorizeResponseClient.getAuthorizeResponse(token) >>
                Mono.error(new OAuth2AuthorizationException(new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR)))

        when:
        manager.authenticate(token).block()

        then:
        def ex = thrown(OAuth2AuthenticationException)
        ex.error.errorCode == OAuth2ErrorCodes.SERVER_ERROR
    }
}
