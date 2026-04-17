package orders.application.port.in;

import java.util.List;
import orders.domain.model.Order;
import orders.domain.model.OrderItem;

/**
 * Puerto de entrada para crear un pedido.
 * Precondicion: command.customerId no nulo y command.items no vacio.
 * Postcondicion: retorna un pedido con ID asignado y estado PENDING.
 */
public interface CreateOrderUseCase {
    record Command(String customerId, List<OrderItem> items) {
    }

    Order execute(Command command);
}
