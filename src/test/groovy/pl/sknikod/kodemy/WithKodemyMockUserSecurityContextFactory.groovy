package pl.sknikod.kodemy


import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.test.context.support.WithSecurityContextFactory
import org.springframework.util.Assert
import org.springframework.util.StringUtils

import java.util.stream.Collectors

class WithKodemyMockUserSecurityContextFactory implements WithSecurityContextFactory<WithKodemyMockUser> {

    @Override
    SecurityContext createSecurityContext(WithKodemyMockUser withUser) {
        final var username = StringUtils.hasLength(withUser.username()) ? withUser.username() : withUser.value()
        Assert.notNull(username, () -> withUser + " cannot have null username on both username and value properties")

        final List<GrantedAuthority> grantedAuthorities = Arrays.stream(withUser.authorities())
                .map(authority -> new SimpleGrantedAuthority(authority))
                .collect(Collectors.toList())

        final var authenticationToken = new OAuth2AuthenticationToken(
                new OAuth2UserImpl(username, grantedAuthorities),
                grantedAuthorities,
                "#test"
        )
        final SecurityContext context = SecurityContextHolder.createEmptyContext()
        context.setAuthentication(authenticationToken)
        return context
    }

    private class OAuth2UserImpl implements OAuth2User {
        final Map<String, Object> attributes = null
        final Collection<? extends GrantedAuthority> authorities
        final String name

        OAuth2UserImpl(String name, Collection<? extends GrantedAuthority> authorities) {
            this.name = name
            this.authorities = authorities
        }
    }
}
