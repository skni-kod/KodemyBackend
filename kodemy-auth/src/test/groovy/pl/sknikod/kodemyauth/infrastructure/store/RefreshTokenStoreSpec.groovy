package pl.sknikod.kodemyauth.infrastructure.store

import pl.sknikod.kodemyauth.factory.UserFactory
import pl.sknikod.kodemyauth.infrastructure.database.RefreshToken
import pl.sknikod.kodemyauth.infrastructure.database.RefreshTokenRepository
import pl.sknikod.kodemyauth.infrastructure.database.UserRepository
import spock.lang.Specification

class RefreshTokenStoreSpec extends Specification {
    def refreshTokenRepository = Mock(RefreshTokenRepository)
    def userRepository = Mock(UserRepository)

    def refreshTokenStore = new RefreshTokenStore(refreshTokenRepository, 1440, userRepository)

    private static final long USER_ID = 1L
    private static final UUID BEARER_ID = UUID.randomUUID()

    def "should create new refresh token"() {
        given:
        userRepository.findById(USER_ID) >> Optional.of(UserFactory.create(USER_ID))
        refreshTokenRepository.save(_ as RefreshToken) >> { RefreshToken refreshToken ->
            {
                refreshToken.setId(1L)
                return refreshToken
            }
        }

        when:
        def result = refreshTokenStore.createAndGet(USER_ID, BEARER_ID)

        then:
        verifyAll(result.get()) {
            id == 1L
            bearerId == BEARER_ID
            user.id == USER_ID
        }
    }
}
