package com.fabio.ias.order_service.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.fabio.ias.order_service.domain.Order;
import reactor.core.publisher.Mono;

public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {

    Mono<Order>  findByRequestId(String requestId);

}
