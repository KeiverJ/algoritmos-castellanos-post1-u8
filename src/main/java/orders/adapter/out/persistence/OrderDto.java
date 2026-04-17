package orders.adapter.out.persistence;

import java.util.List;
import orders.domain.model.Order;
import orders.domain.model.OrderItem;
import orders.domain.model.OrderStatus;

/**
 * DTO de persistencia para desacoplar representacion externa del modelo de dominio.
 */
public record OrderDto(
        String id,
        String customerId,
        List<OrderItemDto> items,
        String status,
        double total
) {
    public static OrderDto fromDomain(Order order) {
        return new OrderDto(
                order.id(),
                order.customerId(),
                order.items().stream()
                        .map(i -> new OrderItemDto(i.productId(), i.quantity(), i.price()))
                        .toList(),
                order.status().name(),
                order.total()
        );
    }

    public Order toDomain() {
        return new Order(
                id,
                customerId,
                items.stream()
                        .map(i -> new OrderItem(i.productId(), i.quantity(), i.price()))
                        .toList(),
                OrderStatus.valueOf(status),
                total
        );
    }

    public record OrderItemDto(String productId, int quantity, double price) {
    }
}
