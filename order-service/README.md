# Descripción Prueba técnica Backend Java + Spring WebFlux
La actual prueba tecnica se desarrollo haciendo uso de tecnologias como Java, Spring Boot, Spring WebFlux, PostgreSQL, R2DBC, SQL,
idempotencia, concurrencia, manejo de errores, Actuator.

Como primera medida se debe crear la BD con nombre "purchase_orders", posteriormente se debe ejecutar los comando de la siguiente sección Ejecutar. Considerando estos items se permite la creación automática de las tablas en BD además de algunos datos de prueba en la tabla de Productos.

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



##### DOCUMENTACIÓN
En primer lugar, quiero agradecer por la oportunidad de participar en esta prueba técnica y de poder demostrar mis conocimientos y capacidades en el desarrollo de software. 

USO DE INTELIGENCIA ARTIFICIAL COMO APOYO =

Durante el desarrollo de la prueba utilicé herramientas de Inteligencia Artificial como apoyo al proceso de investigación, aprendizaje y desarrollo, especialmente teniendo en cuenta que Spring WebFlux y la programación reactiva eran tecnologías con las que no había trabajado previamente.
Uno de los principales usos fue el análisis y construcción de los flujos relacionados con la creación y cancelación de órdenes, donde fue necesario considerar diferentes escenarios como concurrencia, reintentos, errores y respuestas inciertas de un sistema externo. La implementación final fue revisada y adaptada al contexto de la prueba, buscando comprender el funcionamiento de cada componente y no limitarme únicamente a utilizar soluciones generadas automáticamente.

PRINCIPALES CONSIDERACIONES DE LA SOLUCIÓN

Consistencia del inventario ante concurrencia = Para preservar la consistencia del inventario, la actualización del stock se realiza de manera atómica, evitando que dos solicitudes concurrentes puedan descontar unidades sobre el mismo stock disponible.

Prevención de efectos duplicados ante reintentos = Debido a que una misma solicitud puede ser enviada más de una vez, se considera el concepto de idempotencia. Donde `request_id` tiene UNIQUE. El mismo `requestId` no crea una segunda orden ni vuelve a descontar stock. Si dos requests idénticos llegan simultáneamente, la transacción perdedora por UNIQUE revierte su reserva.

Controles de seguridad aplicados = Se pretendía evitar almacenar directamente credenciales y otros datos sensibles dentro del código fuente o archivos, mediante un archivo .env 
