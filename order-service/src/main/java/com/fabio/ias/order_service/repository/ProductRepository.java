package com.fabio.ias.order_service.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.fabio.ias.order_service.domain.Product;

public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {

}
