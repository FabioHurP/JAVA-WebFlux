package com.fabio.ias.order_service.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("orders")
public record Order(@Id Long id, String requestId, Long productId, int quantity, OrderStatus status, ExternalStatus externalStatus,
        String externalIdempotencyKey, String externalLastError, Instant createdAt, Instant updatedAt) {
}