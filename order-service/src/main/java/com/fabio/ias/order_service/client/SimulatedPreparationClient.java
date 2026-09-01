package com.fabio.ias.order_service.client;

import com.fabio.ias.order_service.domain.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.time.Duration;

@Component
public class SimulatedPreparationClient implements PreparationClient {
    private final long delayMs;

    public SimulatedPreparationClient(@Value("${preparation.simulator.delay-ms:150}") long delayMs) {
        this.delayMs = delayMs;
    }

    public Mono<String> prepare(Order o) {
        if (o.requestId().contains("EXTERNAL-FAIL"))
            return Mono.error(new RuntimeException("Simulated external temporary failure"));
        if (o.requestId().contains("EXTERNAL-TIMEOUT"))
            return Mono.delay(Duration.ofSeconds(10)).thenReturn("PREP-" + o.externalIdempotencyKey());
        return Mono.delay(Duration.ofMillis(delayMs)).thenReturn("PREP-" + o.externalIdempotencyKey());
    }
}
