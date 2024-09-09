package pl.sknikod.kodemybackend.exception;

import jakarta.validation.ConstraintViolationException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import pl.sknikod.kodemybackend.BaseTest;
import pl.sknikod.kodemycommon.exception.InternalError500Exception;
import pl.sknikod.kodemycommon.exception.content.ExceptionBody;
import pl.sknikod.kodemycommon.exception.handler.RestExceptionHandler;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class RestExceptionHandlerTest extends BaseTest {
    @Mock
    WebRequest request;

    @Mock
    BindingResult bindingResult;

    MessageSource messageSource = new MessageSourceImpl();

    RestExceptionHandler restExceptionHandler = new RestExceptionHandler();

    @BeforeEach
    void setUp() {
        restExceptionHandler.setMessageSource(messageSource);
    }

    @Test
    void handleConstraintViolationException_shouldBadRequest() {
        // given
        ConstraintViolationException ex = new ConstraintViolationException("Validation failed", null);
        // when
        ResponseEntity<Object> result = restExceptionHandler.handleConstraintViolationException(ex, request);
        // then
        assertNotNull(result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Validation failed", ((ExceptionBody) result.getBody()).getMessage());
    }

    @Test
    void handleAccessDeniedException_shouldForbidden() {
        // given
        AccessDeniedException ex = new AccessDeniedException("Access denied");

        // when
        ResponseEntity<Object> result = restExceptionHandler.handleAccessDeniedException(ex, request);

        // then
        assertNotNull(result.getBody());
        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        assertEquals("Access denied", ((ExceptionBody) result.getBody()).getMessage());
    }

    @Test
    void handleInsufficientAuthenticationException_shouldUnauthorized() {
        // given
        InsufficientAuthenticationException ex = new InsufficientAuthenticationException("Insufficient authentication");
        // when
        ResponseEntity<Object> result = restExceptionHandler.handleInsufficientAuthenticationException(ex, request);
        // then
        assertNotNull(result.getBody());
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertEquals("Insufficient authentication", ((ExceptionBody) result.getBody()).getMessage());
    }

    @Test
    void handleServerProcessingException_shouldServerProcessing() {
        // given
        InternalError500Exception ex = new InternalError500Exception();
        // when
        ResponseEntity<Object> result = restExceptionHandler.handleServerProcessingException(ex, request);
        // then
        assertNotNull(result.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Internal error", ((ExceptionBody) result.getBody()).getMessage());
    }

    @Test
    void handleMethodArgumentNotValid_shouldBadRequest() {
        // given
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("objectName", "field1", "must not be null"),
                new FieldError("objectName", "field2", "must be a valid email")
        ));

        // when
        ResponseEntity<Object> result = restExceptionHandler.handleMethodArgumentNotValid(
                ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, request
        );

        // then
        assertNotNull(result);
        assertNotNull(result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("[field1: must not be null, field2: must be a valid email]", ((ExceptionBody) result.getBody()).getMessage());
    }

    @Test
    void handleExceptionInternal_shouldCorrectResponse() {
        // given
        Exception ex = new Exception("Internal error");
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode statusCode = HttpStatus.INTERNAL_SERVER_ERROR;

        // when
        ResponseEntity<Object> result = restExceptionHandler.handleExceptionInternal(
                ex, null, headers, statusCode, request
        );

        // then
        assertNotNull(result);
        assertNotNull(result.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Internal error", ((ExceptionBody) result.getBody()).getMessage());
    }

    private static class MessageSourceImpl implements MessageSource {
        @Override
        public String getMessage(
                @NotNull String code, Object[] args, String defaultMessage, @NotNull Locale locale) {
            return "";
        }

        @Override
        @NotNull
        public String getMessage(
                @NotNull String code, Object[] args, @NotNull Locale locale) throws NoSuchMessageException {
            return "";
        }

        @Override
        @NotNull
        public String getMessage(
                @NotNull MessageSourceResolvable resolvable, @NotNull Locale locale) throws NoSuchMessageException {
            return "";
        }
    }

}