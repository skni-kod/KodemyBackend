package unit

import spock.lang.Specification

class TSpec extends Specification {
    def "shouldTrue"() {
        given:
        def email = ""
        when:
        email = ""
        then:
        verifyAll {
            email == ""
        }
    }
}
