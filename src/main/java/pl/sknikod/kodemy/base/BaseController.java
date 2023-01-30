package pl.sknikod.kodemy.base;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class BaseController {
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @GetMapping
    public ResponseEntity<List<String>> index() {
        return new ResponseEntity<>(
                requestMappingHandlerMapping
                        .getHandlerMethods()
                        .keySet()
                        .stream()
                        .map(RequestMappingInfo::toString)
                        .filter(endpoint -> !endpoint.contains("/error"))
                        .collect(Collectors.toList()),
                HttpStatus.OK
        );
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(Principal principal) {
        return ResponseEntity.ok("Logged in as: " + principal.getName());
    }
}