package com.fabio.ias.order_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(

    @NotBlank(message = "requestId es obligatorio")
    String requestId,

    @NotNull(message = "productId es obligatorio")
    Long productId,

    @NotNull(message = "quantity es obligatorio")
    @Min(value = 1, message = "quantity debe ser mayor que cero")
    int quantity
) {

}
