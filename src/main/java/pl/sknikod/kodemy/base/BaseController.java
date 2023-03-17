package pl.sknikod.kodemy.base;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import pl.sknikod.kodemy.util.SwaggerResponse;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Tag(name = "Base")
public class BaseController {
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @GetMapping
    @Operation(summary = "Get all API endpoints")
    @SwaggerResponse
    @SwaggerResponse.SuccessCode
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
}