package com.fabio.ias.order_service.exception;

// package com.fabio.ias.orders.web;

import com.fabio.ias.order_service.exception.DomainExceptions.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Traduce excepciones de dominio a respuestas HTTP consistentes, sin filtrar stack traces ni
 * detalles internos. Cada respuesta incluye un traceId correlacionable con los logs (donde si
 * se registra el detalle completo para diagnostico).
 */
@Slf4j
@RestControllerAdvice
@Order(0)
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidation(ValidationException ex) {
        return respond(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", ex.getMessage(), ex);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBindErrors(WebExchangeBindException ex) {
        String message = ex.getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return respond(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message, ex);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleProductNotFound(ProductNotFoundException ex) {
        return respond(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", ex.getMessage(), ex);
    }

    @ExceptionHandler(ProductNotSellableException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleProductNotSellable(ProductNotSellableException ex) {
        return respond(HttpStatus.CONFLICT, "PRODUCT_NOT_SELLABLE", ex.getMessage(), ex);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInsufficientStock(InsufficientStockException ex) {
        return respond(HttpStatus.CONFLICT, "INSUFFICIENT_STOCK", ex.getMessage(), ex);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleOrderNotFound(OrderNotFoundException ex) {
        return respond(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", ex.getMessage(), ex);
    }

    @ExceptionHandler(OrderNotCancellableException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleOrderNotCancellable(OrderNotCancellableException ex) {
        return respond(HttpStatus.CONFLICT, "ORDER_NOT_CANCELLABLE", ex.getMessage(), ex);
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUnexpected(Exception ex) {
        String traceId = UUID.randomUUID().toString();
        log.error("Error no controlado. traceId={}", traceId, ex);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of("INTERNAL_ERROR", "Ocurrio un error inesperado.", traceId)));
    }

    private Mono<ResponseEntity<ErrorResponse>> respond(HttpStatus status, String code, String message, Exception ex) {
        String traceId = UUID.randomUUID().toString();
        log.warn("{} traceId={} detail={}", code, traceId, ex.getMessage());
        return Mono.just(ResponseEntity.status(status).body(ErrorResponse.of(code, message, traceId)));
    }
}
