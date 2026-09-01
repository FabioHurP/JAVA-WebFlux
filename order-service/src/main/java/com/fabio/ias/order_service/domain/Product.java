package com.fabio.ias.order_service.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("products")
public record Product(@Id Long id, String name, boolean enabled, int stock) {
}
