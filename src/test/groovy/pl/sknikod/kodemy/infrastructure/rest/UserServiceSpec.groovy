package pl.sknikod.kodemy.infrastructure.rest


import pl.sknikod.kodemy.MvcIntegrationSpec
import pl.sknikod.kodemy.exception.structure.NotFoundException
import pl.sknikod.kodemy.infrastructure.common.entity.Role
import pl.sknikod.kodemy.infrastructure.common.entity.RoleName
import pl.sknikod.kodemy.infrastructure.common.entity.User
import pl.sknikod.kodemy.infrastructure.common.mapper.UserMapper
import pl.sknikod.kodemy.infrastructure.common.repository.RoleRepository
import pl.sknikod.kodemy.infrastructure.common.repository.UserRepository
import pl.sknikod.kodemy.infrastructure.user.UserService
import pl.sknikod.kodemy.infrastructure.user.rest.UserInfoResponse

class UserServiceSpec extends MvcIntegrationSpec {

    def userRepository = Mock(UserRepository)
    def roleRepository = Mock(RoleRepository)
    def userMapper = Mock(UserMapper)

    def userService = new UserService(userRepository, roleRepository, userMapper)

    def setup(){
        Role role = new Role()
        role.setId(1L)
        role.setName(RoleName.ROLE_ADMIN)
        roleRepository.findByName(_ as RoleName) >> Optional.of(role)

        User user = new User()
        user.setId(1L)
        user.setUsername("Name")
        user.setRole(role)
        userRepository.save(_ as User) >> user

        UserInfoResponse userInfo = new UserInfoResponse(1L, "Name", "email@com.pl","photo", new Date(), new UserInfoResponse.RoleDetails(1L, "Name"))
        userMapper.map(_ as User) >> userInfo
        userService.getUserInfo(_ as Long) >> userInfo
        userService.getUserInfo(_ as Long) >> userInfo
    }

    def "should change roles"() {
        given:
        userService.changeRoles(_ as Long, _ as RoleName) >> null
        Role role = new Role()
        role.setId(1L)
        role.setName(RoleName.ROLE_ADMIN)
        User user = new User()
        user.setId(1L)
        user.setUsername("Name")
        user.setRole(role)
        userRepository.findById(_ as Long) >> Optional.of(user)

        when:
        def result = userService.changeRoles(1L, RoleName.ROLE_ADMIN)

        then:
        result == null
    }

    def "should throw NotFoundException when user does not exists"(){
        given:
        userRepository.findById(_ as Long) >> Optional.empty()

        when:
        userService.changeRoles(1L, RoleName.ROLE_USER)

        then:
        thrown(NotFoundException)
    }

//    def "should get user info"() {
//        when:
//        def result = userService.getUserInfo(1L)
//
//        then:
//        result.getId() == 1l
//        result.getUsername() == "Name"
//    }

    def "should throw NotFoundException when user does not exists in user info"(){
        given:
        userRepository.findById(_ as Long) >> Optional.empty()

        when:
        userService.getUserInfo(1L)

        then:
        thrown(NotFoundException)
    }

}
