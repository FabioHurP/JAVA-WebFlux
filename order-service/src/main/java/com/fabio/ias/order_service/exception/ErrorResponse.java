package com.fabio.ias.order_service.exception;

import java.time.Instant;

public record ErrorResponse(
        String code,
        String message,
        Instant timestamp,
        String traceId
) {
    public static ErrorResponse of(String code, String message, String traceId) {
        return new ErrorResponse(code, message, Instant.now(), traceId);
    }
}

