package pl.sknikod.kodemyauth.infrastructure.module.auth

import spock.lang.Specification

class AccessTokenServiceSpec extends Specification {
    def TOKEN = "eyJhbGciOiJIUzM4NCJ9.eyJpc3MiOiJwbC5za25pa29kLmtvZGVteSIsImp0aSI6ImQxMWI0MDhkLWYwNTMtNDIwMC04YjFkLTE2ODRkYTg4NzEyNCIsInN1YiI6IkthcnRWZW4iLCJpYXQiOjE3MzMyNjU0MDUsImV4cCI6MTczMzMwODYwNSwic3RhdGUiOjgsImF1dGhvcml0aWVzIjpbXSwiaWQiOjF9.qt751MNLNOlLqUkattm3bH0BJtVsmBQBk3g5WTcwBHJTrWNVwhNALv0TQRpRfQQJ"

    def accessTokenService = new AccessTokenService()

    def "should get access token"() {
        when:
        def result = accessTokenService.getAccessToken("Bearer " + TOKEN)

        then:
        result == TOKEN
    }
}
