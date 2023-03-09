package pl.sknikod.kodemy.util;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(implementation = ExceptionRestGenericMessage.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden",
                content = @Content(schema = @Schema(implementation = ExceptionRestGenericMessage.class))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error",
                content = @Content(schema = @Schema(implementation = ExceptionRestGenericMessage.class)))
})
public @interface BaseApiResponses {

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(schema = @Schema(implementation = ExceptionRestGenericMessage.class))
    )
    @interface BadRequest {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @ApiResponse(responseCode = "404", description = "Not Found",
            content = @Content(schema = @Schema(implementation = ExceptionRestGenericMessage.class))
    )
    @interface NotFound {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(schema = @Schema(implementation = ExceptionRestGenericMessage.class))
    )
    @interface Create {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @ApiResponse(responseCode = "404", description = "Not Found",
            content = @Content(schema = @Schema(implementation = ExceptionRestGenericMessage.class))
    )
    @interface Read {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(schema = @Schema(implementation = ExceptionRestGenericMessage.class))
    )
    @ApiResponse(responseCode = "404", description = "Not Found",
            content = @Content(schema = @Schema(implementation = ExceptionRestGenericMessage.class))
    )
    @interface Update {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @ApiResponse(responseCode = "404", description = "Not Found",
            content = @Content(schema = @Schema(implementation = ExceptionRestGenericMessage.class))
    )
    @interface Delete {}
}