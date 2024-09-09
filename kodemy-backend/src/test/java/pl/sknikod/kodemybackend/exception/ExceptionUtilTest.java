package pl.sknikod.kodemybackend.exception;

import org.junit.jupiter.api.Test;
import pl.sknikod.kodemybackend.BaseTest;
import pl.sknikod.kodemycommon.exception.InternalError500Exception;
import pl.sknikod.kodemycommon.exception.content.ExceptionMsgPattern;
import pl.sknikod.kodemycommon.exception.content.ExceptionUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ExceptionUtilTest extends BaseTest {
    private final String MESSAGE = "msg";
    private final String PATTERN = "msg %d";

    @Test
    void throwIfFailure_shouldReturnRuntimeEx_whenRuntimeEx() {
        // given
        var th = new RuntimeException(MESSAGE);
        // when
        var ex = ExceptionUtil.throwIfFailure(th);
        // then
        assertInstanceOf(RuntimeException.class, ex);
        assertEquals(MESSAGE, ex.getMessage());
    }

    @Test
    void throwIfFailure_shouldReturnStructureEx_whenNotRuntimeEx() {
        // given
        var th = new Exception(MESSAGE);
        // when
        var ex = ExceptionUtil.throwIfFailure(th);
        // then
        assertInstanceOf(InternalError500Exception.class, ex);
        assertEquals(ExceptionMsgPattern.INTERNAL_ERROR, ex.getMessage());
    }

    @Test
    void throwIfFailure2_shouldReturnRuntimeEx_whenRuntimeEx() {
        // given
        var arg = 1;
        var th = new RuntimeException(MESSAGE);
        // when
        var ex = ExceptionUtil.throwIfFailure(th, PATTERN, arg);
        // then
        assertInstanceOf(RuntimeException.class, ex);
        assertEquals(MESSAGE, ex.getMessage());
    }

    @Test
    void throwIfFailure2_shouldReturnStructureEx_whenNotRuntimeEx() {
        // given
        var arg = 1;
        var th = new Exception(MESSAGE);
        // when
        var ex = ExceptionUtil.throwIfFailure(th, PATTERN, arg);
        // then
        assertInstanceOf(InternalError500Exception.class, ex);
        assertEquals(String.format(PATTERN, arg), ex.getMessage());
    }
}