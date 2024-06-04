package pl.sknikod.kodemysearch.util.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import pl.sknikod.kodemysearch.exception.ExceptionBody;
import pl.sknikod.kodemysearch.util.BaseTest;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AccessControlExceptionHandlerTest extends BaseTest {
    @Mock
    ObjectMapper objectMapper;
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    PrintWriter printWriter;

    AccessControlExceptionHandler accessControlExceptionHandler;

    @Captor
    ArgumentCaptor<ExceptionBody> exceptionBodyCaptor
            = ArgumentCaptor.forClass(ExceptionBody.class);

    @BeforeEach
    void setUp() throws IOException {
        accessControlExceptionHandler = new AccessControlExceptionHandler(objectMapper);

        when(response.getWriter()).thenReturn(printWriter);
        when(objectMapper.writeValueAsString(any())).thenReturn("");
    }

    @Test
    void entryPoint_shouldWrite_whenAuthenticationException() throws IOException {
        //given
        var exceptionMock = mock(AuthenticationException.class);
        when(objectMapper.writeValueAsString(any()))
                .thenReturn("");
        var statusValue = HttpStatus.UNAUTHORIZED.value();
        //when
        accessControlExceptionHandler.entryPoint(request, response, exceptionMock);
        //then
        verify(response).setStatus(statusValue);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setCharacterEncoding(StandardCharsets.UTF_8.toString());
        verify(objectMapper).writeValueAsString(exceptionBodyCaptor.capture());
        verify(printWriter).write("");

        assertEquals(statusValue, exceptionBodyCaptor.getValue().getStatus());
    }

    @Test
    void accessDenied_shouldWrite_whenAccessDeniedException() throws IOException {
        //given
        var exceptionMock = mock(AccessDeniedException.class);
        when(objectMapper.writeValueAsString(any()))
                .thenReturn("");
        var statusValue = HttpStatus.FORBIDDEN.value();
        //when
        accessControlExceptionHandler.accessDenied(request, response, exceptionMock);
        //then
        verify(response).setStatus(statusValue);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setCharacterEncoding(StandardCharsets.UTF_8.toString());
        verify(objectMapper).writeValueAsString(exceptionBodyCaptor.capture());
        verify(printWriter).write("");

        assertEquals(statusValue, exceptionBodyCaptor.getValue().getStatus());
    }
}