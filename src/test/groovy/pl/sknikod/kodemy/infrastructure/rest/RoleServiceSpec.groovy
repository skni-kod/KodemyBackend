package pl.sknikod.kodemy.infrastructure.rest

import pl.sknikod.kodemy.infrastructure.common.entity.RoleName
import pl.sknikod.kodemy.infrastructure.user.RoleService
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
