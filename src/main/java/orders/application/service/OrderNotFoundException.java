package orders.application.service;

/**
 * Excepcion de negocio para pedidos inexistentes.
 */
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String orderId) {
        super("Order not found: " + orderId);
    }
}
