package pl.sknikod.kodemy.util.filter;

import io.vavr.control.Option;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.sknikod.kodemy.config.AppConfig;
import pl.sknikod.kodemy.user.UserPrincipal;
import pl.sknikod.kodemy.user.UserRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class RefreshUserPrincipalFilter extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppConfig.SecurityRoleProperties roleProperties;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) context.getAuthentication();
            Option.of(authentication)
                    .map(Authentication::getPrincipal)
                    .filter(principal -> principal instanceof UserPrincipal)
                    .map(principal -> (UserPrincipal) principal)
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
                .map(privileges -> {
                    userPrincipal.setAuthorities(privileges);
                    return new OAuth2AuthenticationToken(
                            userPrincipal, privileges,
                            authentication.getAuthorizedClientRegistrationId()
                    );
                })
                .getOrElse(authentication);
    }
}
