package pl.sknikod.kodemyauth.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.OAuth2AuthorizeService;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.OAuth2GetProvidersService;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.exchange.Registration;
import pl.sknikod.kodemycommons.doc.SwaggerResponse;

import java.util.List;
import java.util.Map;

import static org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver.DEFAULT_REGISTRATION_ID_URI_VARIABLE_NAME;

@Tag(name = "Auth")
@SwaggerResponse
@SwaggerResponse.SuccessCode200
public interface OAuth2ControllerDefinition {
    @GetMapping("/api/oauth2/providers")
    @Operation(summary = "Show all OAuth2 providers")
    ResponseEntity<List<OAuth2GetProvidersService.ProviderResponse>> getProvidersList();

    @GetMapping("${app.security.oauth2.endpoint.authorize}/{" + DEFAULT_REGISTRATION_ID_URI_VARIABLE_NAME + "}")
    @Operation(summary = "OAuth2 authorize", description = """
            This endpoint performs the OAuth2 authorization, which is handled by kodemy-api-gateway service.\n
            <b>Executing request here will throws Internal Server Error status.</b>""")
    ResponseEntity<OAuth2AuthorizeService.AuthorizeResponse> authorize(
            @PathVariable(value = DEFAULT_REGISTRATION_ID_URI_VARIABLE_NAME) Registration registration,
            @RequestParam(required = false, defaultValue = "{}") Map<String, String> parameters
    );
}