package com.fabio.ias.order_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.fabio.ias.order_service.dto.CreateOrderRequest;
import com.fabio.ias.order_service.dto.OrderResponse;
import com.fabio.ias.order_service.service.OrderService;

import jakarta.validation.Valid;


import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Mono<ResponseEntity<OrderResponse>> create(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request.requestId(), request.productId(), request.quantity())
                .map(order -> ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order)));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<OrderResponse>> getById(@PathVariable Long id) {
        return orderService.getOrder(id)
                .map(order -> ResponseEntity.ok(OrderResponse.from(order)));
    }

}
