package pl.sknikod.kodemysearch.exception;

import org.junit.jupiter.api.Test;
import pl.sknikod.kodemysearch.exception.structure.ServerProcessingException;
import pl.sknikod.kodemysearch.util.BaseTest;

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
        assertInstanceOf(ServerProcessingException.class, ex);
        assertEquals(ExceptionPattern.INTERNAL_ERROR, ex.getMessage());
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
        assertInstanceOf(ServerProcessingException.class, ex);
        assertEquals(String.format(PATTERN, arg), ex.getMessage());
    }
}