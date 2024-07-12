package pl.sknikod.kodemysearch.util.web;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import pl.sknikod.kodemysearch.exception.ExceptionBody;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@ApiResponse(responseCode = "500", description = "Internal Server Error",
        content = @Content(schema = @Schema(implementation = ExceptionBody.class)))
public @interface SwaggerResponse {
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
    @interface SuccessCode200 {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @ApiResponse(responseCode = "201", description = "Created", useReturnTypeSchema = true)
    @interface CreatedCode201 {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @ApiResponse(responseCode = "202", description = "Accepted", useReturnTypeSchema = true)
    @interface AcceptedCode202 {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(schema = @Schema(implementation = ExceptionBody.class))
    )
    @interface BadRequestCode400 {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ExceptionBody.class))
    )
    @interface UnauthorizedCode401 {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @Content(schema = @Schema(implementation = ExceptionBody.class))
    )
    @interface ForbiddenCode403 {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @ApiResponse(responseCode = "404", description = "Not Found",
            content = @Content(schema = @Schema(implementation = ExceptionBody.class))
    )
    @interface NotFoundCode404 {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @ApiResponse(responseCode = "409", description = "Accepted", useReturnTypeSchema = true)
    @interface ConflictCode409 {
    }

}