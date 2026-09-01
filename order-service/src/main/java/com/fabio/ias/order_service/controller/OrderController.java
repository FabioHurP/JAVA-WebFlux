package com.fabio.ias.order_service.controller;

import com.fabio.ias.order_service.domain.*;
import com.fabio.ias.order_service.dto.CreateOrderRequest;
import com.fabio.ias.order_service.service.OrderService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Mono<ResponseEntity<Order>> create(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request.requestId(), request.productId(), request.quantity())
                .map(order -> ResponseEntity.status(HttpStatus.CREATED).body(order));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Order>> getOrderById(@PathVariable Long id) {
        return orderService.getOrder(id)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/{id}/cancel")
    public Mono<ResponseEntity<Order>> cancel(@PathVariable Long id) {
        return orderService.cancelOrder(id).map(ResponseEntity::ok);
    }

    @PostMapping("/{id}/process")
    public Mono<ResponseEntity<Order>> process(@PathVariable Long id) {
        return orderService.processExternal(id).map(ResponseEntity::ok);
    }

}
