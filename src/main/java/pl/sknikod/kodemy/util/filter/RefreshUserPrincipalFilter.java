package pl.sknikod.kodemy.util.filter;

import io.vavr.control.Option;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.sknikod.kodemy.configuration.AppConfig;
import pl.sknikod.kodemy.infrastructure.model.entity.UserPrincipal;
import pl.sknikod.kodemy.infrastructure.model.repository.UserRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshUserPrincipalFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;
    private final AppConfig.SecurityRoleProperties roleProperties;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() instanceof OAuth2AuthenticationToken authentication) {
            Option.of(authentication)
                    .map(Authentication::getPrincipal)
                    .filter(UserPrincipal.class::isInstance)
                    .map(UserPrincipal.class::cast)
                    .map(userPrincipal -> getoAuth2AuthenticationToken(authentication, userPrincipal))
                    .map(oAuth2AuthenticationToken -> {
                        context.setAuthentication(oAuth2AuthenticationToken);
                        return true;
                    });
        }
        filterChain.doFilter(request, response);
    }

    private OAuth2AuthenticationToken getoAuth2AuthenticationToken(OAuth2AuthenticationToken authentication, UserPrincipal userPrincipal) {
        return Option
                .of(userPrincipal.getId())
                .map(userRepository::findById)
                .map(Optional::get)
                .map(user -> user.getRole().getName())
                .filter(roleName ->
                        userPrincipal.getRole() != null && !roleName.equals(userPrincipal.getRole().getName())
                )
                .map(roleProperties::getPrivileges)
                .map(privileges -> new OAuth2AuthenticationToken(
                        UserPrincipal.create(userPrincipal, privileges), privileges,
                        authentication.getAuthorizedClientRegistrationId()
                ))
                .getOrElse(authentication);
    }
}
