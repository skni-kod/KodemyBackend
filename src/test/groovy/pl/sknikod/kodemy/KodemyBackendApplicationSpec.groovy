package pl.sknikod.kodemy

import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY

@AutoConfigureEmbeddedDatabase(beanName = "dataSource", provider = ZONKY)
@SpringBootTest
class KodemyBackendApplicationSpec extends Specification {

    def "should load context"() {
        expect:
        true
    }
}