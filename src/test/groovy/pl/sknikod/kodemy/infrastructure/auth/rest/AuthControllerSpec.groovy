package pl.sknikod.kodemy.infrastructure.auth.rest


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.web.util.UriComponentsBuilder
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
import pl.sknikod.kodemy.MvcIntegrationSpec
import pl.sknikod.kodemy.configuration.AppConfig
import pl.sknikod.kodemy.infrastructure.auth.AuthService
import pl.sknikod.kodemy.infrastructure.common.entity.UserProviderType

import javax.servlet.http.HttpServletRequest
import java.util.function.Consumer

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(AuthController)
class AuthControllerSpec extends MvcIntegrationSpec {

    private static final String DOMAIN = "http://localhost:8181"
    private static final MediaType MEDIA_TYPE_TEXT_PLAIN = MediaType.parseMediaType("text/plain;charset=UTF-8")

    @Autowired
    AuthService authService

    @Autowired
    AppConfig.SecurityAuthProperties authProperties

    def "should return url for authorization via Github"() {
        given:
        def url = "${DOMAIN + authProperties.getLoginUri()}/github?redirect_uri=${DOMAIN}"
        authService.getLink(_ as HttpServletRequest, _ as Consumer<UriComponentsBuilder>, _ as String) >> url


        when:
        def result = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/oauth2/link-for-authorize-" + UserProviderType.GITHUB)
                        .param("redirect_uri", DOMAIN)
        )

        then:
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MEDIA_TYPE_TEXT_PLAIN))
                .andExpect(content().string(url))
    }

    def "should return url for logout for oAuth2"() {
        given:
        def url = "${DOMAIN + authProperties.getLogoutUri()}?redirect_uri=${DOMAIN}"
        authService.getLink(_ as HttpServletRequest, _ as Consumer<UriComponentsBuilder>, _ as String) >> url

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/api/oauth2/link-for-logout")
                .param("redirect_uri", DOMAIN)
        )

        then:
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MEDIA_TYPE_TEXT_PLAIN))
                .andExpect(content().string(url))
    }

    def "should return list of providers"() {
        given:
        authService.getProvidersList() >> UserProviderType.values()

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/api/oauth2/providers"))

        then:
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(UserProviderType.values())))
    }
}
