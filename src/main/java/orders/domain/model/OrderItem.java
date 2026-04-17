package orders.domain.model;

/**
 * Value object de item de pedido.
 */
public record OrderItem(String productId, int quantity, double price) {
}
