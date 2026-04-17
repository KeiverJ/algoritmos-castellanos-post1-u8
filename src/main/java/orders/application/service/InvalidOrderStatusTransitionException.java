package orders.application.service;

import orders.domain.model.OrderStatus;

/**
 * Excepcion de negocio para transiciones de estado no permitidas.
 */
public class InvalidOrderStatusTransitionException extends RuntimeException {
    public InvalidOrderStatusTransitionException(String orderId, OrderStatus current, OrderStatus next) {
        super("Invalid status transition for order " + orderId + ": " + current + " -> " + next);
    }
}
