package pl.sknikod.kodemy.util;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import pl.sknikod.kodemy.exception.ExceptionRestGenericMessage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@ApiResponse(responseCode = "500", description = "Internal Server Error",
        content = @Content(schema = @Schema(implementation = ExceptionRestGenericMessage.class)))
public @interface SwaggerResponse {
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
    @interface SuccessCode {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @ApiResponse(responseCode = "201", description = "Created", useReturnTypeSchema = true)
    @interface CreatedCode {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @ApiResponse(responseCode = "202", description = "Accepted", useReturnTypeSchema = true)
    @interface AcceptedCode {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(schema = @Schema(implementation = ExceptionRestGenericMessage.class))
    )
    @interface BadRequestCode {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ExceptionRestGenericMessage.class))
    )
    @interface UnauthorizedCode {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @Content(schema = @Schema(implementation = ExceptionRestGenericMessage.class))
    )
    @interface ForbiddenCode {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @ApiResponse(responseCode = "404", description = "Not Found",
            content = @Content(schema = @Schema(implementation = ExceptionRestGenericMessage.class))
    )
    @interface NotFoundCode {
    }

}