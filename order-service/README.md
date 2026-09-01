# Descripción Prueba técnica Backend Java + Spring WebFlux
La actual prueba tecnica se desarrollo haciendo uso de tecnologias como Java, Spring Boot, Spring WebFlux, PostgreSQL, R2DBC, SQL,
idempotencia, concurrencia, manejo de errores, Actuator.

Como primera medida se debe crear la BD con nombre "purchase_orders", despues se debe ejecutar los comando de la siguiente seccion Ejecutar

############### Ejecutar
Requisitos: Java 21, Maven y Docker.
```bash
docker compose up -d
mvn spring-boot:run


############### Endpoints
Health:

```text
GET http://localhost:8080/actuator/health

Crear:

```bash
curl -X POST http://localhost:8080/api/orders
    -H "Content-Type: application/json"
    -d '{"requestId":"REQ-001","productId":1,"quantity":2}'
```

Consultar orden:

```text
GET /api/orders/{id}
```

Cancelar:

```text
POST /api/orders/{id}/cancel
```

Procesar externo:

```text
POST /api/orders/{id}/process
```

Inventario:

```text
GET /api/products/{id}



############### Arquitectura

```text
Cliente -> WebFlux Controller -> OrderService -> R2DBC/PostgreSQL
                                      |
                                      +-> PreparationClient -> sistema externo simulado

