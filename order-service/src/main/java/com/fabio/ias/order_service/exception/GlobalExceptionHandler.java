package com.fabio.ias.order_service.exception;
import com.fabio.ias.order_service.dto.ApiError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    Mono<ResponseEntity<ApiError>> business(BusinessException e, ServerWebExchange x) {
        return Mono.just(ResponseEntity.status(e.getStatus()).body(new ApiError(Instant.now(), e.getStatus().value(),
                e.getStatus().getReasonPhrase(), e.getMessage(), x.getRequest().getPath().value())));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    Mono<ResponseEntity<ApiError>> validation(WebExchangeBindException e, ServerWebExchange x) {
        String m = e.getFieldErrors().stream().map(f -> f.getField() + ": " + f.getDefaultMessage()).findFirst()
                .orElse("Invalid request");
        return Mono.just(ResponseEntity.badRequest()
                .body(new ApiError(Instant.now(), 400, "Bad Request", m, x.getRequest().getPath().value())));
    }

    @ExceptionHandler(Exception.class)
    Mono<ResponseEntity<ApiError>> generic(Exception e, ServerWebExchange x) {
        return Mono.just(ResponseEntity.internalServerError().body(new ApiError(Instant.now(), 500,
                "Internal Server Error", "Unexpected error. Check logs.", x.getRequest().getPath().value())));
    }
}
