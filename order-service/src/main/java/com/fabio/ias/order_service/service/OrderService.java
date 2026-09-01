package com.fabio.ias.order_service.service;

import com.fabio.ias.order_service.client.PreparationClient;
import com.fabio.ias.order_service.domain.*;
import com.fabio.ias.order_service.exception.BusinessException;
import com.fabio.ias.order_service.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import java.time.*;


@Service
public class OrderService {

    private static final Duration EXTERNAL_TIMEOUT = Duration.ofSeconds(3);
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductStockRepository productStockRepository;
    private final PreparationClient external;
    private final TransactionalOperator tx;


    public OrderService(OrderRepository orderRepository,
                         ProductRepository productRepository,
                         ProductStockRepository productStockRepository,
                         TransactionalOperator tx, PreparationClient external) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.productStockRepository = productStockRepository;
        this.tx = tx;
        this.external = external;
    }

    public Mono<Order> getOrder(Long orderId) {
        return orderRepository.findById(orderId)
        .switchIfEmpty(Mono.error(new BusinessException(HttpStatus.NOT_FOUND, "Order not found")));
    }


     public Mono<Order> createOrder(String requestId, Long productId, int quantity) {
        return orderRepository.findByRequestId(requestId)
                .switchIfEmpty(Mono.defer(() -> createNewOrder(requestId, productId, quantity).onErrorResume(
                        e -> isDuplicate(e) ? orderRepository.findByRequestId(requestId).switchIfEmpty(Mono.error(e))
                                : Mono.error(e))));
    }

    private Mono<Order> createNewOrder(String requestId, Long productId, int quantity) {
        // La cantidad debe ser mayor que cero
        if (quantity <= 0) {
            return Mono.error(new BusinessException(HttpStatus.BAD_REQUEST, "quantity must be greater than zero"));
        }
        return productRepository.findById(productId)
                // El producto debe existir para la venta.
                .switchIfEmpty(Mono.error(new BusinessException(HttpStatus.NOT_FOUND, "Product doesn't exist")))
                .flatMap(p -> {
                    // El producto debe encontrarse habilitado para la venta.
                    if (!p.enabled()) {
                        return Mono.error(new BusinessException(HttpStatus.CONFLICT, "Product is disabled"));
                    }
                    //No puede aceptarse una cantidad superior al stock
                    return productStockRepository.reserve(productId, quantity).flatMap(rows -> {
                        if (rows != 1) {
                            return Mono.error(new BusinessException(HttpStatus.CONFLICT, "Insufficient stock"));
                        }
                        Instant now = Instant.now();
                        return orderRepository.save(new Order(null, requestId, productId, quantity, OrderStatus.ACCEPTED, ExternalStatus.PENDING,
                                requestId, null, now, now));
                    });
                }).as(tx::transactional);
    }


    private boolean isDuplicate(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            String m = t.getMessage();
            if (m != null && (m.toLowerCase().contains("duplicate") || m.toLowerCase().contains("unique")))
                return true;
        }
        return false;
    }


    public Mono<Order> cancelOrder(Long id) {
        return orderRepository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(HttpStatus.NOT_FOUND, "Order not found")))
                .flatMap(o -> {
                    if (OrderStatus.CANCELLED.equals(o.status())){
                        return Mono.error(new BusinessException(HttpStatus.CONFLICT, "Order is already cancelled"));
                    }
                    if (!ExternalStatus.PENDING.equals(o.externalStatus())) {
                        return Mono.error(new BusinessException(HttpStatus.CONFLICT,
                                "Order cannot be cancelled because external processing is not pending"));
                    }
                    return productStockRepository.release(o.productId(), o.quantity()).flatMap(r -> {
                        if (r != 1) {
                            return Mono
                                    .error(new BusinessException(HttpStatus.CONFLICT, "Product could not be updated"));
                        }
                        return orderRepository.save(copy(o, OrderStatus.CANCELLED, ExternalStatus.CANCELLED, null));
                    });
                }).as(tx::transactional);
    }

    private Order copy(Order o, OrderStatus status, ExternalStatus ext, String err) {
        return new Order(o.id(), o.requestId(), o.productId(), o.quantity(), status, ext, o.externalIdempotencyKey(),
                err, o.createdAt(), Instant.now());
    }


    public Mono<Order> processExternal(Long id) {
        return claim(id).flatMap(o -> external.prepare(o).timeout(EXTERNAL_TIMEOUT).flatMap(ext -> markConfirmed(id))
                .onErrorResume(e -> markUncertain(id, safe(e))));
    }

    private Mono<Order> claim(Long id) {
        return orderRepository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(HttpStatus.NOT_FOUND, "Order not found")))
                .flatMap(o -> {
                    if (OrderStatus.CANCELLED.equals(o.status()))
                        return Mono.error(
                                new BusinessException(HttpStatus.CONFLICT, "Cancelled order cannot be processed"));
                    if (ExternalStatus.CONFIRMED.equals(o.externalStatus()))
                        return Mono.just(o);
                    if (ExternalStatus.PROCESSING.equals(o.externalStatus()))
                        return Mono
                                .error(new BusinessException(HttpStatus.CONFLICT, "Order is already being processed"));
                    return orderRepository.save(copy(o, o.status(), ExternalStatus.PROCESSING, null));
                }).as(tx::transactional);
    }

    private Mono<Order> markConfirmed(Long id) {
        return orderRepository.findById(id).flatMap(o -> orderRepository.save(copy(o, o.status(), ExternalStatus.CONFIRMED, null)))
                .as(tx::transactional);
    }

    private Mono<Order> markUncertain(Long id, String err) {
        return orderRepository.findById(id).flatMap(o -> orderRepository.save(copy(o, o.status(), ExternalStatus.UNCERTAIN, err)))
                .as(tx::transactional);
    }

    private String safe(Throwable e) {
        return e.getClass().getSimpleName() + ": "
                + (e.getMessage() == null ? "external processing failed" : e.getMessage());
    }


}
