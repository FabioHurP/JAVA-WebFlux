package com.fabio.ias.order_service.repository;

import reactor.core.publisher.Mono;

public interface ProductStockRepository {
     Mono<Long> reserve(Long productId, int quantity);

    Mono<Long> release(Long productId, int quantity);

}
