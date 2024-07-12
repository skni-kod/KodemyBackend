package pl.sknikod.kodemyauth.util.route;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RouteRedirectStrategy extends DefaultRedirectStrategy {
    @SneakyThrows
    public void sendRedirect(
            HttpServletRequest request, HttpServletResponse response,
            String location, MultiValueMap<String, String> params) {
        String uri = UriBuilder.location(location).params(params).build().toString();
        super.sendRedirect(request, response, uri);
    }

    @SneakyThrows
    public void sendRedirect(
            HttpServletRequest request, HttpServletResponse response,
            String location, Map<String, String> params) {
        String uri = UriBuilder.location(location).params(params).build().toString();
        super.sendRedirect(request, response, uri);
    }

    private record UriBuilder(UriComponentsBuilder locationBuilder) {
        public static UriBuilder location(String location) {
            return new UriBuilder(convert(location));
        }

        private static UriComponentsBuilder convert(String location) {
            return UriComponentsBuilder.fromUriString(location.length() <= 2 ? location.replace("//", "/") : location);
        }

        public UriBuilder params(MultiValueMap<String, String> params) {
            params.forEach((key, values) -> values.forEach(value -> locationBuilder.queryParam(key, value)));
            return this;
        }

        public UriBuilder params(Map<String, String> params) {
            params.forEach(locationBuilder::queryParam);
            return this;
        }

        public URI build() {
            return locationBuilder.build().toUri();
        }
    }
}
