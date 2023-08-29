package pl.sknikod.kodemy

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.test.web.servlet.MockMvc
import pl.sknikod.kodemy.exception.ExceptionRestHandler
import pl.sknikod.kodemy.infrastructure.rest.RoleService
import pl.sknikod.kodemy.infrastructure.rest.SectionService
import pl.sknikod.kodemy.infrastructure.rest.TechnologyService
import pl.sknikod.kodemy.infrastructure.rest.UserService
import spock.lang.Specification
import spock.mock.DetachedMockFactory

@Import([TestSecurityConfig, BeanConfig])
@WebMvcTest(ExceptionRestHandler)
abstract class MvcIntegrationSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @TestConfiguration
    static class BeanConfig {
        def detachedMockFactory = new DetachedMockFactory()

        @Bean
        TechnologyService technologyService() {
            return detachedMockFactory.Mock(TechnologyService)
        }
        @Bean
        SectionService sectionService(){
            return detachedMockFactory.Mock(SectionService)
        }
        @Bean
        RoleService roleService(){
            return detachedMockFactory.Mock(RoleService)
        }
        @Bean
        UserService userService(){
            return detachedMockFactory.Mock(UserService)
        }
    }

    @TestConfiguration
    @EnableWebSecurity
    @EnableGlobalMethodSecurity(
            prePostEnabled = true,
            securedEnabled = true
    )
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) {
            http
                    .authorizeHttpRequests(autz -> autz
                            .anyRequest().permitAll()
                    )
                    .httpBasic(Customizer.withDefaults())
                    .formLogin().disable()
                    .oauth2Login().disable()
                    .csrf().disable()
            return http.build()
        }
    }
}