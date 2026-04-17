package orders.domain.model;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad Order, nucleo del dominio, sin dependencias externas.
 * Invariante: items no vacio y total mayor o igual a cero.
 */
public record Order(
        String id,
        String customerId,
        List<OrderItem> items,
        OrderStatus status,
        double total
) {
    public Order {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(customerId, "customerId is required");
        Objects.requireNonNull(items, "items are required");
        Objects.requireNonNull(status, "status is required");
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        if (total < 0) {
            throw new IllegalArgumentException("Order total cannot be negative");
        }
        items = List.copyOf(items);
    }

    public static Order create(String customerId, List<OrderItem> items) {
        Objects.requireNonNull(customerId, "customerId is required");
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }

        double total = items.stream()
                .mapToDouble(i -> i.price() * i.quantity())
                .sum();

        return new Order(
                UUID.randomUUID().toString(),
                customerId,
                List.copyOf(items),
                OrderStatus.PENDING,
                total
        );
    }

    public Order withStatus(OrderStatus newStatus) {
        return new Order(id, customerId, items, newStatus, total);
    }
}
