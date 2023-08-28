package pl.sknikod.kodemy.infrastructure.rest

import pl.sknikod.kodemy.infrastructure.model.entity.RoleName
import spock.lang.Specification

class RoleServiceSpec extends Specification {

    def roleService = new RoleService()

    def "GetRoles"() {
        when:
        def result = roleService.getRoles()

        then:
        result == RoleName.values()
    }
}
