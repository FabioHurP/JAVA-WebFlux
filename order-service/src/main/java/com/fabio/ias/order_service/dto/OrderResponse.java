package com.fabio.ias.order_service.dto;

import com.fabio.ias.order_service.domain.Order;
import com.fabio.ias.order_service.domain.OrderStatus;

import java.time.Instant;

public record OrderResponse(
        Long id,
        String requestId,
        Long productId,
        int quantity,
        OrderStatus status,
        Instant createdDate,
        Instant updatedDate
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getRequestId(),
                order.getProductId(),
                order.getQuantity(),
                order.getStatus(),
                order.getCreatedDate(),
                order.getUpdatedDate()
        );
    }
}

