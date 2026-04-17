package orders.application.port.in;

import orders.domain.model.Order;
import orders.domain.model.OrderStatus;

/**
 * Puerto de entrada para actualizar estado de pedido.
 * Precondicion: transicion de estado valida.
 * Postcondicion: retorna el pedido actualizado.
 */
public interface UpdateOrderStatusUseCase {
    record Command(String orderId, OrderStatus newStatus) {
    }

    Order execute(Command command);
}
