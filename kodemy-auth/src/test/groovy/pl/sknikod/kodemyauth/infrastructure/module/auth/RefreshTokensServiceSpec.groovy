package pl.sknikod.kodemyauth.infrastructure.module.auth

import io.vavr.control.Try
import pl.sknikod.kodemyauth.factory.RefreshTokenFactory
import pl.sknikod.kodemyauth.factory.RoleFactory
import pl.sknikod.kodemyauth.infrastructure.database.RoleRepository
import pl.sknikod.kodemyauth.infrastructure.store.RefreshTokenStore
import pl.sknikod.kodemycommons.security.JwtProvider
import spock.lang.Specification

import java.time.Instant

class RefreshTokensServiceSpec extends Specification {
    def roleRepository = Mock(RoleRepository)
    def refreshTokenStore = Mock(RefreshTokenStore)
    def jwtProvider = Mock(JwtProvider)

    def refreshTokensService = new RefreshTokensService(
            roleRepository, refreshTokenStore, jwtProvider
    )

    static final UUID REFRESH = UUID.randomUUID()
    static final UUID BEARER_JTI = UUID.randomUUID()

    def "should refresh token"() {
        given:
        def oldRefreshToken = RefreshTokenFactory.create(REFRESH, BEARER_JTI)
        refreshTokenStore.findByTokenAndBearerJti(REFRESH, BEARER_JTI) >> Try.success(oldRefreshToken)
        roleRepository.findById(_ as Long) >> Optional.of(RoleFactory.create())
        var newTokenId = UUID.randomUUID()
        jwtProvider.generateUserToken(_)
                >> new JwtProvider.Token(newTokenId, "TOKEN", Date.from(Instant.now()))
        refreshTokenStore.createAndGet(_, _)
                >> Try.success(RefreshTokenFactory.create(newTokenId, UUID.randomUUID()))
        refreshTokenStore.invalidate(oldRefreshToken) >> {}

        when:
        def result = refreshTokensService.refresh(REFRESH, BEARER_JTI)

        then:
        result.refresh() == newTokenId.toString()
    }
}
