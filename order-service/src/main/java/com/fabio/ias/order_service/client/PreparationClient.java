package com.fabio.ias.order_service.client;

import com.fabio.ias.order_service.domain.Order;
import reactor.core.publisher.Mono;

public interface PreparationClient {
    Mono<String> prepare(Order order);
}
