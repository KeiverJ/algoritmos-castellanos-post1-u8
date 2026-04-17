package orders.application.service;

import java.util.Objects;
import orders.application.port.in.CreateOrderUseCase;
import orders.application.port.in.GetOrderUseCase;
import orders.application.port.in.UpdateOrderStatusUseCase;
import orders.application.port.out.OrderRepository;
import orders.domain.model.Order;

/**
 * Implementacion de casos de uso de pedidos.
 * Depende solo de puertos, nunca de infraestructura concreta.
 */
public class OrderService implements
        CreateOrderUseCase, GetOrderUseCase, UpdateOrderStatusUseCase {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository is required");
    }

    @Override
    public Order execute(CreateOrderUseCase.Command cmd) {
        Objects.requireNonNull(cmd, "command is required");
        Order order = Order.create(cmd.customerId(), cmd.items());
        return repository.save(order);
    }

    @Override
    public Order execute(String orderId) {
        return repository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Override
    public Order execute(UpdateOrderStatusUseCase.Command cmd) {
        Objects.requireNonNull(cmd, "command is required");

        Order order = repository.findById(cmd.orderId())
                .orElseThrow(() -> new OrderNotFoundException(cmd.orderId()));

        if (!order.status().canTransitionTo(cmd.newStatus())) {
            throw new InvalidOrderStatusTransitionException(
                    order.id(),
                    order.status(),
                    cmd.newStatus()
            );
        }

        Order updated = order.withStatus(cmd.newStatus());
        return repository.save(updated);
    }
}
