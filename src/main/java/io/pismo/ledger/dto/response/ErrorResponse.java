package io.pismo.ledger.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.OffsetDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String error,
        String message,
        OffsetDateTime timestamp,
        List<FieldError> fieldErrors
) {
    public ErrorResponse(int status, String error, String message) {
        this(status, error, message, OffsetDateTime.now(), null);
    }

    public ErrorResponse(int status, String error, String message, List<FieldError> fieldErrors) {
        this(status, error, message, OffsetDateTime.now(), fieldErrors);
    }

    public record FieldError(String field, String message) {
    }
}
