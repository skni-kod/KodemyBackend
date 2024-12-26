package pl.sknikod.kodemygateway.infrastructure.module.oauth2.handler

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal
import org.springframework.security.oauth2.core.OAuth2RefreshToken
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.web.server.ServerWebExchange
import pl.sknikod.kodemygateway.util.AuthCookies
import spock.lang.Specification

class AuthenticationSuccessHandlerSpec extends Specification {
    private static final FRONT_BASE_URL = "http://localhost:3000"
    private static final ACCESS_TOKEN_AGE = 30
    private static final REFRESH_TOKEN_AGE = 60

    def authenticationSuccessHandler = new AuthenticationSuccessHandler(FRONT_BASE_URL, ACCESS_TOKEN_AGE, REFRESH_TOKEN_AGE)

    private static final ACCESS_TOKEN
            = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWF0IjoxNTE2MjM5MDIyfQ.L8i6g3PfcHlioHCCPURC9pmXT7gdJpx3kOoyAfNUwCc";
    private static final REFRESH_TOKEN = UUID.randomUUID().toString();

    def "should set cookies and redirect"() {
        given:
        def token = Mock(OAuth2AuthenticationToken) {
            getPrincipal() >> Mock(DefaultOAuth2User) {
                getAttributes() >> [
                        accessToken : Mock(OAuth2AccessToken) {
                            getTokenValue() >> ACCESS_TOKEN
                        },
                        refreshToken: Mock(OAuth2RefreshToken) {
                            getTokenValue() >> REFRESH_TOKEN
                        }
                ]
            }
        }

        def headers = new HttpHeaders()
        def serverHttpResponse = Mock(ServerHttpResponse) {
            getHeaders() >> headers
        }
        def webFilterExchange = Mock(WebFilterExchange) {
            getExchange() >> Mock(ServerWebExchange) {
                getResponse() >> serverHttpResponse
            }
        }

        when:
        authenticationSuccessHandler.onAuthenticationSuccess(webFilterExchange, token)

        then:
        1 * serverHttpResponse.setStatusCode(HttpStatus.FOUND)
        headers.getLocation().toString() == "$FRONT_BASE_URL?auth=success"

        and: "check cookies"
        headers.get(HttpHeaders.SET_COOKIE).size() == 2
        def cookie1 = headers.get(HttpHeaders.SET_COOKIE).find { it.startsWith(AuthCookies.ACCESS_TOKEN) }
        cookie1.contains(ACCESS_TOKEN)
        cookie1.contains("Max-Age=${ACCESS_TOKEN_AGE * 60}")
        def cookie2 = headers.get(HttpHeaders.SET_COOKIE).find { it.startsWith(AuthCookies.REFRESH_TOKEN) }
        cookie2.contains(REFRESH_TOKEN)
        cookie2.contains("Max-Age=${REFRESH_TOKEN_AGE * 60}")
    }
}
