package pl.sknikod.kodemy


import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification

@Testcontainers
class KodemyBackendApplicationSpec extends Specification {

    @Shared
    PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14.1-alpine")

    def "should load app context"() {
        expect:
        true
    }
}
