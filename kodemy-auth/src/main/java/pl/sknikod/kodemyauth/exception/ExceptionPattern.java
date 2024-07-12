package pl.sknikod.kodemyauth.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionPattern {
    public static final String INTERNAL_ERROR = "Internal error";
    public static final String PROCESS_FAILED_ENTITY = "Failed to process %s";

    public static final String ENTITY_NOT_FOUND = "%s not found";
    public static final String ENTITY_NOT_FOUND_BY_PARAM = "%s not found %s(%s)";

    public static final String ENTITY_ALREADY_EXISTS = "%s already exists";

    public static final String NOT_AUTHORIZED = "Not authorized";
}