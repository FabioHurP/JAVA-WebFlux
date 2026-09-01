package com.fabio.ias.order_service.repository;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public class ProductStockRepositoryImpl implements ProductStockRepository {
    private final DatabaseClient db;

    public ProductStockRepositoryImpl(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Long> reserve(Long id, int q) {
        return db.sql("""
                UPDATE products
                SET stock = stock - :quantity
                WHERE id = :productId
                  AND enabled = true
                  AND stock >= :quantity
                """)
                .bind("quantity", q)
                .bind("productId", id)
                .fetch()
                .rowsUpdated();
    }

    public Mono<Long> release(Long id, int q) {
        return db.sql("""
                UPDATE products
                SET stock = stock + :quantity
                WHERE id = :productId
                """)
                .bind("quantity", q)
                .bind("productId", id)
                .fetch()
                .rowsUpdated();
    }

}
