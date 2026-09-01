package com.fabio.ias.order_service.service;

import org.springframework.stereotype.Service;
import com.fabio.ias.order_service.domain.Order;
import com.fabio.ias.order_service.exception.DomainExceptions.*;
import com.fabio.ias.order_service.repository.OrderRepository;
import com.fabio.ias.order_service.repository.ProductRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;


    public OrderService(OrderRepository orderRepository,
                         ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public Mono<Order> getOrder(Long orderId) {
        return orderRepository.findById(orderId)
        .switchIfEmpty(Mono.error(new OrderNotFoundException(orderId)));
    }


    public Mono<Order> createOrder(String requestId, Long productId, int quantity) {
        if (quantity <= 0) {
            return Mono.error(new IllegalArgumentException("The amount must be greater than zero"));
        }

        return Mono.error(new IllegalArgumentException("The amount must be greater than zero"));
    }



}
