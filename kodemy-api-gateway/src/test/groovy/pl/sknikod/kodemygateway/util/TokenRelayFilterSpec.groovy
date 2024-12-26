package pl.sknikod.kodemygateway.util

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.HttpCookie
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import spock.lang.Specification

class TokenRelayFilterSpec extends Specification {
    private static final refreshTokenUri = "http://kodemy-auth:8080/api/auth/access_token"
    def webClient = Mock(WebClient)
    def objectMapper = new ObjectMapper()

    def filter = new TokenRelayFilter(webClient, objectMapper, refreshTokenUri, 30, 60)
    def chain = Mock(GatewayFilterChain)
    def exchange = Mock(ServerWebExchange)

    def setup() {
        def serverHttpRequestBuilder = Mock(ServerHttpRequest.Builder);
        exchange.getRequest() >> Mock(ServerHttpRequest) {
            mutate() >> serverHttpRequestBuilder
        }
        serverHttpRequestBuilder.header(_, _) >> serverHttpRequestBuilder
        chain.filter(exchange) >> Mono.just(new Object())
    }

    def "should exit when access token is null"() {
        given:
        exchange.getRequest().getCookies() >> new LinkedMultiValueMap<>();

        when:
        filter.filter(exchange, chain)

        then:
        1 * chain.filter(exchange)
    }

    def "should exit when access token is expired and refresh token is null"() {
        given:
        def accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE1MTYyMzkwMjJ9.zY5wLQZ0tFNAChdB6wU0PbaIy90ZrM02umYNqJTLrfI"
        exchange.getRequest().getCookies() >> new LinkedMultiValueMap<>(Map.of(
                AuthCookies.ACCESS_TOKEN, List.of(new HttpCookie(AuthCookies.ACCESS_TOKEN, accessToken))
        ))

        when:
        filter.filter(exchange, chain)

        then:
        1 * chain.filter(exchange)
    }

    def "should refresh access token when access token is expired"() {
        given:
        def accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE1MTYyMzkwMjJ9.zY5wLQZ0tFNAChdB6wU0PbaIy90ZrM02umYNqJTLrfI"
        exchange.getRequest().getCookies() >> new LinkedMultiValueMap<>(Map.of(
                AuthCookies.ACCESS_TOKEN, List.of(new HttpCookie(AuthCookies.ACCESS_TOKEN, accessToken)),
                AuthCookies.REFRESH_TOKEN, List.of(new HttpCookie(AuthCookies.REFRESH_TOKEN, UUID.randomUUID().toString()))
        ))
        exchange.getResponse() >> Mock(ServerHttpResponse) {
            getHeaders() >> new HttpHeaders()
        }

        webClient.post() >> Mock(WebClient.RequestBodyUriSpec) {
            uri(_ as URI) >> Mock(WebClient.RequestBodySpec) {
                retrieve() >> Mock(WebClient.ResponseSpec) {
                    bodyToMono(TokenRelayFilter.RefreshTokensResponse.class) >>
                            Mono.just(new TokenRelayFilter.RefreshTokensResponse("refresh", "token"));
                }
            }
        }

        when:
        filter.filter(exchange, chain).block()

        then:
        verifyAll(exchange.getResponse().getHeaders().get(HttpHeaders.SET_COOKIE)) {
            size() == 2
        }
    }

    def "should exit when access token is valid"() {
        given:
        def accessToken =
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.nrgGyuP_sMlQKcXQiGMtmv94MlDXrUiguuq3lgrsddk"
        exchange.getRequest().getHeaders() >> new HttpHeaders()
        exchange.getRequest().getCookies() >> new LinkedMultiValueMap<>(Map.of(
                AuthCookies.ACCESS_TOKEN, List.of(new HttpCookie(AuthCookies.ACCESS_TOKEN, accessToken))
        ))

        when:
        filter.filter(exchange, chain)

        then:
        1 * chain.filter(exchange)
    }

    def "should handle invalid JWT structure"() {
        given:
        def token = "invalid-token"
        exchange.getRequest().getCookies() >> new LinkedMultiValueMap<>(Map.of(
                AuthCookies.ACCESS_TOKEN, List.of(new HttpCookie(AuthCookies.ACCESS_TOKEN, token))
        ))

        and:
        objectMapper.readValue(_, _) >> { throw new RuntimeException("Invalid JWT structure") }

        when:
        filter.filter(exchange, chain)

        then:
        1 * chain.filter(exchange)
    }
}