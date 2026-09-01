package com.fabio.ias.order_service.exception;

/**
 * Excepciones de dominio. Se agrupan en un solo archivo por brevedad; en un proyecto mas
 * grande cada una iria en su propio archivo.
 */
public class DomainExceptions {

    private DomainExceptions() {}

    public static class ValidationException extends RuntimeException {
        public ValidationException(String message) { super(message); }
    }

    public static class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(Long productId) {
            super("Producto no encontrado: " + productId);
        }
    }

    public static class ProductNotSellableException extends RuntimeException {
        public ProductNotSellableException(Long productId) {
            super("Producto no habilitado para venta: " + productId);
        }
    }

    public static class InsufficientStockException extends RuntimeException {
        public InsufficientStockException(Long productId) {
            super("Stock insuficiente para el producto: " + productId);
        }
    }

    public static class OrderNotFoundException extends RuntimeException {
        public OrderNotFoundException(Long orderId) {
            super("Orden no encontrada: " + orderId);
        }
    }

    public static class OrderNotCancellableException extends RuntimeException {
        public OrderNotCancellableException(Long orderId, String currentStatus) {
            super("La orden " + orderId + " no admite cancelacion en su estado actual: " + currentStatus);
        }
    }

}
