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
import pl.sknikod.kodemy.configuration.AppConfig
import pl.sknikod.kodemy.exception.ExceptionRestHandler
import pl.sknikod.kodemy.infrastructure.auth.AuthService
import pl.sknikod.kodemy.infrastructure.category.CategoryService
import pl.sknikod.kodemy.infrastructure.material.MaterialCreateUseCase
import pl.sknikod.kodemy.infrastructure.material.MaterialService
import pl.sknikod.kodemy.infrastructure.search.SearchService
import pl.sknikod.kodemy.infrastructure.section.SectionService
import pl.sknikod.kodemy.infrastructure.technology.TechnologyService
import pl.sknikod.kodemy.infrastructure.type.TypeService
import pl.sknikod.kodemy.infrastructure.user.UserPrincipalUseCase
import spock.lang.Specification
import spock.mock.DetachedMockFactory

@Import([TestSecurityConfig, BeanConfig, AppConfig.SecurityAuthProperties])
@WebMvcTest(ExceptionRestHandler)
abstract class MvcIntegrationSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @TestConfiguration
    static class BeanConfig {
        def detachedMockFactory = new DetachedMockFactory()

        @Bean
        TypeService typeService() {
            return detachedMockFactory.Mock(TypeService)
        }

        @Bean
        SectionService sectionService() {
            return detachedMockFactory.Mock(SectionService)
        }

        @Bean
        AuthService authService() {
            return detachedMockFactory.Mock(AuthService)
        }

        @Bean
        MaterialService materialService() {
            return detachedMockFactory.Mock(MaterialService)
        }

        @Bean
        UserPrincipalUseCase userPrincipalUseCase() {
            return detachedMockFactory.Mock(UserPrincipalUseCase)
        }

        @Bean
        CategoryService categoryService() {
            return detachedMockFactory.Mock(CategoryService)
        }

        @Bean
        SearchService searchService() {
            return detachedMockFactory.Mock(SearchService)
        }

        @Bean
        MaterialCreateUseCase materialCreateUseCase() {
            return detachedMockFactory.Mock(MaterialCreateUseCase)
        }

        @Bean
        TechnologyService technologyService() {
            return detachedMockFactory.Mock(TechnologyService)
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