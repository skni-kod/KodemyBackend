package pl.sknikod.kodemygateway.util

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import spock.lang.Specification

class LogoutRelayGatewayFilterFactorySpec extends Specification {
    def "should modify headers"() {
        given:
        def headers = new HttpHeaders();
        def exchange = Mock(ServerWebExchange) {
            getResponse() >> Mock(ServerHttpResponse) {
                getStatusCode() >> HttpStatus.OK
                getHeaders() >> headers
            }
            getAttribute(_ as String) >> new Object()
        }
        def chain = Mock(GatewayFilterChain) {
            filter(exchange) >> Mono.empty()
        }

        when:
        new LogoutRelayGatewayFilterFactory.LogoutRelayFilter().filter(exchange, chain).block()

        then:
        headers.size() == 1
        def cookie1 = headers.get(HttpHeaders.SET_COOKIE).find { it.startsWith(AuthCookies.ACCESS_TOKEN) }
        cookie1.contains("")
        cookie1.contains("Max-Age=0")
        def cookie2 = headers.get(HttpHeaders.SET_COOKIE).find { it.startsWith(AuthCookies.REFRESH_TOKEN) }
        cookie2.contains("")
        cookie2.contains("Max-Age=0")
    }

    def "should not modify headers when CLIENT_RESPONSE_CONN_ATTR is null"() {
        given:
        def headers = new HttpHeaders();
        def exchange = Mock(ServerWebExchange) {
            getResponse() >> Mock(ServerHttpResponse) {
                getHeaders() >> headers
            }
            getAttribute(_ as String) >> null
        }
        def chain = Mock(GatewayFilterChain) {
            filter(exchange) >> Mono.empty()
        }

        when:
        new LogoutRelayGatewayFilterFactory.LogoutRelayFilter().filter(exchange, chain).block()

        then:
        headers.size() == 0
    }
}
