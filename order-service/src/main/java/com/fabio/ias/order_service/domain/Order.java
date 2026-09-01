package com.fabio.ias.order_service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    private Long id;

    /** Identificador de la solicitud generado por el canal. Clave de idempotencia de negocio. */
    private String requestId;

    private Long productId;

    private int quantity;

    private OrderStatus status;

    private Instant createdDate;

    private Instant updatedDate;
}
