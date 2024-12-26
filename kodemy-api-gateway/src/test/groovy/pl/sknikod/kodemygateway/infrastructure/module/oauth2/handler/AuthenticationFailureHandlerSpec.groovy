package pl.sknikod.kodemygateway.infrastructure.module.oauth2.handler

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2ErrorCodes
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.web.server.ServerWebExchange
import spock.lang.Specification

class AuthenticationFailureHandlerSpec extends Specification {
    private static final FRONT_BASE_URL = "http://localhost:3000"

    def authenticationFailureHandler = new AuthenticationFailureHandler(FRONT_BASE_URL);

    def "should redirect with error parameter"(OAuth2Error error) {
        given:
        def exception = new OAuth2AuthenticationException(error)

        def serverHttpResponse = Mock(ServerHttpResponse) {
            getHeaders() >> new HttpHeaders()
        }
        def webFilterExchange = Mock(WebFilterExchange) {
            getExchange() >> Mock(ServerWebExchange) {
                getResponse() >> serverHttpResponse
            }
        }

        when:
        authenticationFailureHandler.onAuthenticationFailure(webFilterExchange, exception)

        then:
        1 * serverHttpResponse.setStatusCode(HttpStatus.FOUND)
        serverHttpResponse.getHeaders()
                .getLocation().toString() == FRONT_BASE_URL + "?auth=failure&error=" + error.getErrorCode()

        where:
        error | _
        new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR) | _
        new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN) | _
    }

    def "should redirect with general error"() {
        given:
        def exception = new InsufficientAuthenticationException("")

        def serverHttpResponse = Mock(ServerHttpResponse) {
            getHeaders() >> new HttpHeaders()
        }
        def webFilterExchange = Mock(WebFilterExchange) {
            getExchange() >> Mock(ServerWebExchange) {
                getResponse() >> serverHttpResponse
            }
        }

        when:
        authenticationFailureHandler.onAuthenticationFailure(webFilterExchange, exception)

        then:
        1 * serverHttpResponse.setStatusCode(HttpStatus.FOUND)
        serverHttpResponse.getHeaders()
                .getLocation().toString() == FRONT_BASE_URL + "?auth=failure&error=server_error"
    }
}
